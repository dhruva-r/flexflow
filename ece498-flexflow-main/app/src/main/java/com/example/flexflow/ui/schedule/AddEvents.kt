package com.example.flexflow.ui.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.flexflow.data.entity.EventEntity
import com.example.flexflow.data.entity.TaskEntity
import com.example.flexflow.data.room.EventDao
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

class AddEvents {

    // We activate this from AddTaskViewModel ...
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addEvent(task : TaskEntity, eventDao: EventDao){
        // Create the final event first (the due date event)
        val cal = Calendar.getInstance()
        cal.time = task.dueDate

        val finalEvent = EventEntity(
            name = task.name + " due",
            details = task.details,
            startDate = task.startDate,
            endDate = task.endDate,
            taskId = task.id,
            priority = 1.0,
            eventCompletion = false,
            taskCompletion = false,
            day = cal.get(Calendar.DATE),
            month = cal.get(Calendar.MONTH),
            year = cal.get(Calendar.YEAR)
        )

        eventDao.insert(finalEvent)

        val max : Int = (task.commitment*4).toInt()
        val len : Int = (task.complexity*4).toInt()

        /*
         *  Function that we call here called schedule event (where we insert an
         *   event on a day and it handles the logic of picking the time and fixing the rest of the
         *   scheduled events
         */

        val day = Calendar.getInstance()
        if(daysBetween(task.dueDate) > 6){
            day.time = task.dueDate
            day.add(Calendar.DATE, -6)
        }
        day.set(Calendar.HOUR_OF_DAY, 8)
        day.set(Calendar.MINUTE, 0)
        day.set(Calendar.SECOND, 0)

        val dueDate = Calendar.getInstance()
        dueDate.time = task.dueDate
        if (!((max == 1) && (len == 1))){
            for (x in 1..len){
                day.add(Calendar.DATE, 1)
                if(day.time < dueDate.time){
                    if(dueDate.get(Calendar.DATE) == day.get(Calendar.DATE)
                        && dueDate.get(Calendar.MONTH) == day.get(Calendar.MONTH)
                        && dueDate.get(Calendar.YEAR) == dueDate.get(Calendar.YEAR)){
                        break;
                    }
                    scheduleEvent(day, task, eventDao)
                }
            }
        }
    }

    private suspend fun scheduleEvent(ca: Calendar, task: TaskEntity, eventDao: EventDao) {
        // Make new end time of event based on the commitment
        val newEventEnd = Calendar.getInstance()
        newEventEnd.time = ca.time
        newEventEnd.add(Calendar.HOUR_OF_DAY, (task.commitment*4).toInt())

        // Create the new event
        val newEvent = EventEntity(
            name = task.name,
            details = task.details,
            startDate = ca.time,
            endDate = newEventEnd.time,
            taskId = task.id,
            priority = task.priority,
            eventCompletion = false,
            taskCompletion = false,
            day = ca.get(Calendar.DATE),
            month = ca.get(Calendar.MONTH),
            year = ca.get(Calendar.YEAR)
        )

        // Insert the new event into the list of events at the correct position
        eventDao.insert(newEvent)
        // Sort the events by priority and return the list
        val updatedEvents = eventDao.getEventsFromDateListPrio(
            ca.get(Calendar.DATE),
            ca.get(Calendar.MONTH),
            ca.get(Calendar.YEAR)
        )
        // Adjust the schedule to resolve conflicts
        if(updatedEvents.size >1){
            createSchedule(updatedEvents, eventDao)
        }

    }

    private suspend fun adjustSchedule(events: List<EventEntity>, eventDao: EventDao) {
        // Iterate through each event and adjust schedule if there are conflicts
        for ((index, currentEvent) in events.withIndex()) {
            if (index == 0) {
                // For the first event, check for conflicts with the second event
                val nextEvent = events[1]
                if (currentEvent.endDate > nextEvent.startDate) {
                    // Adjust start time of the first event to end time of the second event
                    val newStartTime = Calendar.getInstance()
                    newStartTime.time = nextEvent.endDate
                    currentEvent.startDate = newStartTime.time
                    currentEvent.endDate = calculateNewEndTime(currentEvent, eventDao)
                    // Update the adjusted event in the database
                    eventDao.update(currentEvent)
                }
            } else if (index == events.size - 1) {
                // For the last event, check for conflicts with the previous event
                val previousEvent = events[index - 1]
                if (previousEvent.endDate > currentEvent.startDate) {
                    // Adjust end time of the last event to start time of the previous event
                    val newEndTime = Calendar.getInstance()
                    newEndTime.time = previousEvent.startDate
                    currentEvent.endDate = newEndTime.time
                    currentEvent.startDate = calculateNewStartTime(currentEvent, eventDao)
                    // Update the adjusted event in the database
                    eventDao.update(currentEvent)
                }
            } else {
                // For events in between, check conflicts with both previous and next events
                val previousEvent = events[index - 1]
                val nextEvent = events[index + 1]

                // Adjust start time if conflicts with previous event
                if (previousEvent.endDate > currentEvent.startDate) {
                    val newStartTime = Calendar.getInstance()
                    newStartTime.time = previousEvent.endDate
                    currentEvent.startDate = newStartTime.time
                    currentEvent.endDate = calculateNewEndTime(currentEvent, eventDao)
                }

                // Adjust end time if conflicts with next event
                if (currentEvent.endDate > nextEvent.startDate) {
                    val newEndTime = Calendar.getInstance()
                    newEndTime.time = nextEvent.startDate
                    currentEvent.endDate = newEndTime.time
                    currentEvent.startDate = calculateNewStartTime(currentEvent, eventDao)
                }

                // Update the adjusted event in the database
                eventDao.update(currentEvent)
            }
        }
    }

    private suspend fun calculateNewEndTime(event: EventEntity, eventDao: EventDao): Date {
        val calendar = Calendar.getInstance()
        calendar.time = event.startDate
        val overlappingEvents = eventDao.getEventsBetweenDates(calendar.time, event.endDate)
        val totalDurationOfOverlappingEvents = overlappingEvents.sumOf { calculateDurationInMinutes(it) }
        calendar.add(Calendar.MINUTE, totalDurationOfOverlappingEvents)
        return calendar.time
    }

    private suspend fun calculateNewStartTime(event: EventEntity, eventDao: EventDao): Date {
        val calendar = Calendar.getInstance()
        calendar.time = event.endDate
        val overlappingEvents = eventDao.getEventsBetweenDates(event.startDate, calendar.time)
        val totalDurationOfOverlappingEvents = overlappingEvents.sumOf { calculateDurationInMinutes(it) }
        calendar.add(Calendar.MINUTE, -totalDurationOfOverlappingEvents)
        return calendar.time
    }

    private fun calculateDurationInMinutes(event: EventEntity): Int {
        val durationInMillis = event.endDate.time - event.startDate.time
        return (durationInMillis / (1000 * 60)).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysBetween(ca: Date): Int{
        val current = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            time = ca
        }
        return ChronoUnit.DAYS.between(current.toInstant(), calendar.toInstant()).toInt()
    }

    public suspend fun createSchedule(events: List<EventEntity>, eventDao: EventDao){
        val calendar = Calendar.getInstance()
        val startDates : MutableList<Date> = mutableListOf()
        val endDates : MutableList<Date> = mutableListOf()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.DATE, events[0].day)
        calendar.set(Calendar.MONTH, events[0].month)
        calendar.set(Calendar.YEAR, events[0].year)
        for (event in events){
            if(event.priority == 1.0){
                startDates.add(event.startDate)
                endDates.add(event.endDate)
            } else{
                val x = calculateDurationInMinutes(event)
                event.startDate = calendar.time
                calendar.add(Calendar.MINUTE, x)
                event.endDate = calendar.time
                for(i in 0..<startDates.size){
                    if (detectConflict(event.startDate, event.endDate, startDates[i], endDates[i])){
                        calendar.time = endDates[i]
                        event.startDate = calendar.time
                        calendar.add(Calendar.MINUTE, x)
                        event.endDate = calendar.time
                    }
                }
                eventDao.update(event)
            }
        }
    }

    private fun detectConflict(s1: Date, e1: Date, s2: Date, e2: Date) : Boolean{
        return if(s2.time <= s1.time && s1.time < e2.time){
            true
        } else if(s2.time <= e1.time && e1.time < e2.time){
            true
        } else if(s1.time <= s2.time && e1.time >= e2.time){
            true
        } else {
            false
        }
    }



}