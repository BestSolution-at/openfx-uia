# OpenFX-UIA Changelog

## Release 1.2.3 (2025-03-13)

### Changes

- fix calling glass on AdviseEvent* API even if it is an uia accessible

## Release 1.2.2 (2024-10-22)

### Changes

- fix missing commit for previous release

## Release 1.2.1 (2024-10-21)

### Changes

- `IUIAVirtualRootElement` children are now forcefully created.

## Release 1.2.0 (2024-09-09)

### Changes

- ElementProviderFromPoint now correctly hides all javafx accessibles below an `IUIAVirtualRootElement`

- New API: `IUIAVirtualRootElement#getFocus()`
- JavaFx scene accessible now delegates focus handling to `IUIAVirtualRootElement#getFocus()`

- Logging improvments
