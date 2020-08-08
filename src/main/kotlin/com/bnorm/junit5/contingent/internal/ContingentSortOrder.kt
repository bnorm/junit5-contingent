package com.bnorm.junit5.contingent.internal

import com.bnorm.junit5.contingent.Contingent
import org.junit.jupiter.api.MethodDescriptor
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.MethodOrdererContext
import org.junit.jupiter.api.Tag
import java.util.ArrayDeque

internal class ContingentSortOrder : MethodOrderer {
    override fun orderMethods(context: MethodOrdererContext) {
        val nodes = context.methodDescriptors.map { descriptor ->
            val gatedAnnotation: Contingent? = descriptor.method.getAnnotation(Contingent::class.java)
            val tagAnnotation: Array<Tag> = descriptor.method.getAnnotationsByType(Tag::class.java)
            Node(descriptor, gatedAnnotation?.values?.toList() ?: emptyList(), tagAnnotation.map { it.value })
        }

        val nodesBySatisfies = nodes.flatMap { node -> node.satisfies.map { it to node } }.groupBy({ it.first }, { it.second })
        for (node in nodes) {
            val dependencies = node.requires.flatMap { nodesBySatisfies[it] ?: emptyList() }

            val circularPaths = dependencies.asSequence()
                .flatMap { it.dependencyPaths() }
                .filter { node == it.nodes.lastOrNull() }
                .toList()
            if (circularPaths.isNotEmpty()) {
                val paths = circularPaths.joinToString("\n") { path ->
                    path.nodes.joinToString(" -> ", prefix = "\t") { node -> node.descriptor.method.toGenericString() }
                }
                throw IllegalStateException("Found circular contingent tests, behavior is undetermined:\n$paths")
            }

            node.depends.addAll(dependencies)
        }

        val remaining = ArrayDeque(nodes)
        val added = mutableMapOf<Node, Int>()
        var index = 0
        while (remaining.isNotEmpty()) {
            val node = remaining.pop()
            if (added.keys.containsAll(node.depends)) {
                added[node] = index++
            } else {
                remaining.add(node)
            }
        }

        val descriptors = added.mapKeys { (key, _) -> key.descriptor }
        context.methodDescriptors.sortBy { descriptors[it] }
    }

    private class Node(
        val descriptor: MethodDescriptor,
        val requires: List<String>,
        val satisfies: List<String>
    ) {
        val depends = mutableListOf<Node>()
    }

    private class Path(
        val nodes: List<Node>
    )

    private operator fun Path?.plus(node: Node) = if (this == null) Path(listOf(node)) else Path(this.nodes + listOf(node))
    private operator fun Path?.plus(path: Path) = if (this == null) path else Path(this.nodes + path.nodes)

    private fun Node.dependencyPaths(prefix: Path? = null): Sequence<Path> {
        val root = this
        return sequence {
            val rootPath = prefix + root
            yield(rootPath)
            for (dependency in depends) {
                yieldAll(dependency.dependencyPaths(rootPath))
            }
        }
    }
}
