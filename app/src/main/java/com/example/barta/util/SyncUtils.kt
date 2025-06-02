package com.example.barta.util

import com.example.barta.util.Step
import com.example.barta.util.TranscriptItem

fun groupTranscriptByStep(
    steps: List<Step>,
    transcripts: List<TranscriptItem>
): List<Pair<Step, List<TranscriptItem>>> {
    return steps.map { step ->
        val segment = transcripts.filter {
            it.start >= step.startTime && it.start < step.endTime
        }
        step to segment
    }
}

fun mergeTranscriptText(transcripts: List<TranscriptItem>): String {
    return transcripts.joinToString(" ") { it.text }
}
