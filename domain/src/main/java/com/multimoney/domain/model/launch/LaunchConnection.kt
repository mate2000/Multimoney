package com.multimoney.domain.model.launch

data class LaunchConnection(
    val cursor: String,
    val hasMore: Boolean,
    val launches: List<Launch?>
)