package com.github.stepbeekio.todo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository: CrudRepository<Todo, Long>, PagingAndSortingRepository<Todo, Long> {
}
