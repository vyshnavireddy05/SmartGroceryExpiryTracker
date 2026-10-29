export function notifyAuthenticationChanged() {
    window.dispatchEvent(new Event("authentication-changed"));
}
