use tauri::{
  plugin::{Builder, TauriPlugin},
  Manager, Runtime,
};

pub use models::*;

#[cfg(desktop)]
mod desktop;
#[cfg(mobile)]
mod mobile;

mod commands;
mod error;
mod models;

pub use error::{Error, Result};

#[cfg(desktop)]
use desktop::ImeInsets;
#[cfg(mobile)]
use mobile::ImeInsets;

/// Extensions to [`tauri::App`], [`tauri::AppHandle`] and [`tauri::Window`] to access the ime-insets APIs.
pub trait ImeInsetsExt<R: Runtime> {
  fn ime_insets(&self) -> &ImeInsets<R>;
}

impl<R: Runtime, T: Manager<R>> crate::ImeInsetsExt<R> for T {
  fn ime_insets(&self) -> &ImeInsets<R> {
    self.state::<ImeInsets<R>>().inner()
  }
}

/// Initializes the plugin.
pub fn init<R: Runtime>() -> TauriPlugin<R> {
  Builder::new("ime-insets")
    .invoke_handler(tauri::generate_handler![commands::ping])
    .setup(|app, api| {
      #[cfg(mobile)]
      let ime_insets = mobile::init(app, api)?;
      #[cfg(desktop)]
      let ime_insets = desktop::init(app, api)?;
      app.manage(ime_insets);
      Ok(())
    })
    .build()
}
