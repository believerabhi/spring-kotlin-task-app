package com.abhi.spring_task_app.controller

import com.abhi.spring_task_app.data.model.Priority
import com.abhi.spring_task_app.data.model.TaskDto
import com.abhi.spring_task_app.data.model.TaskUpdateRequest
import com.abhi.spring_task_app.service.TaskService

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
internal class TaskControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var mockService: TaskService

    private val taskId: Long = 33
    private val dummyDto1 = TaskDto(
            33,
            "test1",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW
    )
    private val mapper = jacksonObjectMapper()
    @BeforeEach
    fun setUp() {
        mapper.registerModule(JavaTimeModule())
    }

    @Test
    fun `given all tasks when fetch happen then check for size`() {
        // GIVEN
        val taskDto2 = TaskDto(
                44,
                "test2",
                isReminderSet = false,
                isTaskOpen = false,
                createdOn = LocalDateTime.now(),
                priority = Priority.LOW
        )
        val expectedDtos: List<TaskDto> = listOf(dummyDto1, taskDto2)

        // WHEN
        `when`(mockService.getAllTasks()).thenReturn(expectedDtos)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/all-tasks"))

        // THEN
        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(expectedDtos.size))
    }

    @Test
    fun `given one task when get task by id is called with string instead of int then check for bad request`() {
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/task/404L"))

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `given update task request when task gets updated then check for correct property`() {
        val request = TaskUpdateRequest(
                "update task",
                isReminderSet = false,
                isTaskOpen = false,
                priority = Priority.LOW
        )
        val dummyDto = TaskDto(
                44,
                request.description ?: "",
                isReminderSet = false,
                isTaskOpen = false,
                createdOn = LocalDateTime.now(),
                priority = Priority.LOW
        )

        `when`(mockService.updateTask(dummyDto.id, request)).thenReturn(dummyDto)
        val resultActions: ResultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/update/${dummyDto.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.description").value(dummyDto.description))
    }
}