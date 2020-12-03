function formatDate(date) {
    return date.getUTCFullYear() + "/" +
        ("0" + (date.getUTCMonth() + 1)).slice(-2) + "/" +
        ("0" + date.getUTCDate()).slice(-2) + " " +
        ("0" + date.getUTCHours()).slice(-2) + ":" +
        ("0" + date.getUTCMinutes()).slice(-2) + ":" +
        ("0" + date.getUTCSeconds()).slice(-2);
}

function setTheme(themeName) {
    localStorage.setItem('theme', themeName);
    document.documentElement.className = themeName;
}

function toggleTheme() {
    if (localStorage.getItem('theme') === 'theme-dark') {
        setTheme('theme-light');
    } else {
        setTheme('theme-dark');
    }
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

(function () {
    if (localStorage.getItem('theme') === 'theme-light') {
        setTheme('theme-light');
    } else {
        setTheme('theme-dark');
    }

    let style = document.documentElement.style;

    let primary = getCookie("color-primary");
    if (!primary)
        primary = "#0060df";

    let secondary = getCookie("color-secondary");
    if (!secondary)
        secondary = "#222222";

    let tertiary = getCookie("color-tertiary");
    if (!tertiary)
        tertiary = "#111111";

    let accent = getCookie("color-accent");
    if (!accent)
        accent = "#12cdea";

    let fontc = getCookie("color-font");
    if (!fontc)
        fontc = "#cccccc";

    style.setProperty("--color-primary", primary);
    style.setProperty("--color-secondary", secondary);
    style.setProperty("--color-tertiary", tertiary);
    style.setProperty("--color-accent", accent);
    style.setProperty("--font-color", fontc);
})();