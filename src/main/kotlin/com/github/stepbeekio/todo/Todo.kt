package com.github.stepbeekio.todo

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

enum class TodoStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    ;
}

@Table("todo")
data class Todo(
    @Id
    var identifier: Long,
    var content: String,
    val createdAt: Instant,
    @LastModifiedDate
    var updatedAt: Instant,
    var status: TodoStatus,
) {
    companion object {
        val alphabet = ('a'..'z') + ' ' + ('A'..'Z')
        fun new(content: String): Todo =
            Todo(
                identifier = 0L,
                content = content,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                status = TodoStatus.TODO,
            )

        fun random(): Todo = Todo.new(
            (1..4000).map { alphabet.random() }.joinToString("")
        )
    }
}
