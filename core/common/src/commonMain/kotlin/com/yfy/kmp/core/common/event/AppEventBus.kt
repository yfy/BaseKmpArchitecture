package com.yfy.kmp.core.common.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

public sealed interface AppEvent {
    public data object LoggedOut : AppEvent

    public data object SessionExpired : AppEvent
}

public class AppEventBus {
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 16)
    public val events: Flow<AppEvent> = _events.asSharedFlow()

    public fun emit(event: AppEvent) {
        _events.tryEmit(event)
    }
}
