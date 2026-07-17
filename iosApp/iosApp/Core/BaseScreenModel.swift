import SwiftUI
import Shared

// iOS has no ViewModelStoreOwner, so deinit is the only hook for clearing a Kotlin store's scope.
final class VMOwner<VM: AnyObject> {
    let vm: VM
    private let onClear: () -> Void

    init(_ vm: VM, onClear: @escaping () -> Void) {
        self.vm = vm
        self.onClear = onClear
    }

    deinit { onClear() }
}

// Wraps the Kotlin store rather than subclassing it: ObjC interop erases generics.
// For the same reason the store's own `state`/`effects` are unusable from Swift — the flows must be
// passed in from the shared module's concrete-typed accessors (StateBridge.kt).
@MainActor
class BaseScreenModel<Store: AnyObject, State: AnyObject, Effect: AnyObject>: ObservableObject {
    private let owner: VMOwner<Store>
    private var stateTask: Task<Void, Never>?
    private var effectTask: Task<Void, Never>?

    @Published var state: State

    var store: Store { owner.vm }

    init(
        store: Store,
        clear: @escaping (Store) -> Void,
        state stateFlow: SkieSwiftStateFlow<State>,
        effects effectFlow: SkieSwiftFlow<Effect>
    ) {
        self.owner = VMOwner(store, onClear: { clear(store) })
        self.state = stateFlow.value
        self.stateTask = Task { [weak self] in
            for await newState in stateFlow { self?.state = newState }
        }
        self.effectTask = Task { [weak self] in
            for await effect in effectFlow { self?.handleEffect(effect) }
        }
    }

    func handleEffect(_ effect: Effect) {}

    deinit {
        stateTask?.cancel()
        effectTask?.cancel()
    }
}

@MainActor
class StateScreenModel<Store: AnyObject, State: AnyObject>: ObservableObject {
    private let owner: VMOwner<Store>
    private var task: Task<Void, Never>?

    @Published var state: State

    var store: Store { owner.vm }

    init(
        store: Store,
        clear: @escaping (Store) -> Void,
        state stateFlow: SkieSwiftStateFlow<State>
    ) {
        self.owner = VMOwner(store, onClear: { clear(store) })
        self.state = stateFlow.value
        self.task = Task { [weak self] in
            for await newState in stateFlow { self?.state = newState }
        }
    }

    deinit { task?.cancel() }
}
