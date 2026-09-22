use tauri::{AppHandle, command, Runtime};

use crate::models::*;
use crate::Result;
use crate::ImeInsetsExt;

#[command]
pub(crate) async fn ping<R: Runtime>(
    app: AppHandle<R>,
    payload: PingRequest,
) -> Result<PingResponse> {
    app.ime_insets().ping(payload)
}
