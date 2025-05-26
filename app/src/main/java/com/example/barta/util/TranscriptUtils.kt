package com.example.barta.util

fun syncTranscriptToSteps(
    steps: List<Step>,
    transcripts: List<TranscriptItem>
): List<StepWithTranscript> {
    return steps.map { step ->
        val matched = transcripts.filter { transcript ->
            transcript.start >= step.startTime && transcript.start < step.endTime
        }
        StepWithTranscript(step, matched)
    }
}
