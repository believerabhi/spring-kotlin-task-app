package com.abhi.spring_task_app.service

import com.abhi.spring_task_app.data.Task
import com.abhi.spring_task_app.data.model.Priority
import com.abhi.spring_task_app.data.model.TaskCreateRequest
import com.abhi.spring_task_app.data.model.TaskDto
import com.abhi.spring_task_app.exception.BadRequestException
import com.abhi.spring_task_app.exception.TaskNotFoundException
import com.abhi.spring_task_app.repository.TaskRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

//ref here - https://medium.com/@habibicoding/kotlin-spring-boot-tutorial-part-5-creating-rest-endpoints-for-a-task-app-f8f8b4a4567e
@ExtendWith(MockKExtension::class)
class TaskServiceTest {

    @RelaxedMockK
    private lateinit var mockRepository: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskService

    private val task = Task()
    private lateinit var createRequest: TaskCreateRequest

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createRequest = TaskCreateRequest(
                "test task",
                isReminderSet = false,
                isTaskOpen = false,
                createdOn = LocalDateTime.now(),
                priority = Priority.LOW
        )
    }

    @Test
    fun `when all tasks get fetched then check if the given size is correct`() {
        // GIVEN
        val expectedTasks = listOf(Task(), Task())

        // WHEN
        every { mockRepository.findAll() } returns expectedTasks.toMutableList()
        val actualList: List<TaskDto> = objectUnderTest.getAllTasks()

        // THEN
        assertThat(actualList.size).isEqualTo(expectedTasks.size)
    }

    @Test
    fun `when task gets created then check if it gets properly created`() {
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.createdOn = createRequest.createdOn

        every { mockRepository.save(any()) } returns task
        val actualTaskDto: TaskDto = objectUnderTest.createTask(createRequest)

        assertThat(actualTaskDto.description).isEqualTo(task.description)
    }

    @Test
    fun `when task gets created with non unique description then check for bad request exception`() {
        every { mockRepository.doesDescriptionExist(any()) } returns true

        val exception = assertThrows<BadRequestException> { objectUnderTest.createTask(createRequest) }

        assertThat(exception.message).isEqualTo("There is already a task with description: test task")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when get task by id is called then expect a task not found exception`() {
        every { mockRepository.existsById(any()) } returns false

        val exception = assertThrows<TaskNotFoundException> { objectUnderTest.getTaskById(123) }

        assertThat(exception.message).isEqualTo("Task with ID: 123 does not exist!")
        verify { mockRepository.findTaskById(any()) wasNot called }
    }
}