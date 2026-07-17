package com.yfy.kmp.core.common.base

import kotlinx.coroutines.flow.Flow

public abstract class BaseUseCase<in P, out R> {
    public abstract suspend operator fun invoke(parameters: P): R
}

public abstract class BaseFlowUseCase<in P, out R> {
    public abstract operator fun invoke(parameters: P): Flow<R>
}

public interface BaseMapper<D, M> {
    public fun toModel(dto: D): M
    public fun fromModel(model: M): D
}
