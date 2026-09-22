# Tauri Plugin ime-insets

Install by adding
```
tauri-plugin-ime-insets = { git = "https://github.com/Silas230899/tauri-plugin-ime-insets", branch = "main" }
```
to your Cargo-toml [dependencies] and adding
```
.plugin(tauri_plugin_ime_insets::init())
```
to the
```
tauri::Builder::default()
```
in lib.rs
