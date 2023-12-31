package com.payamgr.qrcodemaker.data.model.state

import com.airbnb.mvrx.MavericksState
import com.payamgr.qrcodemaker.data.model.Content

data class HomeState(
    val contents: List<Content> = listOf(),
) : MavericksState
