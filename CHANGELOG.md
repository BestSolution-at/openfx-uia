# OpenFX-UIA Changelog

## Release 1.2.0 (TBD)

### Changes

- ElementProviderFromPoint now correctly hides all javafx accessibles below an `IUIAVirtualRootElement`

- New API: `IUIAVirtualRootElement#getFocus()`
- JavaFx scene accessible now delegates focus handling to `IUIAVirtualRootElement#getFocus()`

- Logging improvments
