// to set theme in local storage
function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

// to get theme from local storage
function getTheme() {
    let theme = localStorage.getItem("theme");
    return (theme) ? theme : "light";
}

let currentTheme = getTheme();

// change theme only when content uploaded completely
document.addEventListener('DOMContentLoaded', () => {
    changeTheme();
});


// function to change theme
function changeTheme() {
    // set to web page
    document.querySelector("html").classList.add(currentTheme);

    // set the listener to change the theme button
    const themeChangeButton = document.querySelector("#theme_change_button");

    // change the text of button
    themeChangeButton.querySelector('span').textContent = currentTheme == "light" ? "Dark" : "Light";

    themeChangeButton.addEventListener("click", (event) => {

        const oldTheme = currentTheme;

        if (currentTheme === "light") {
            currentTheme = "dark";
        } else {
            currentTheme = "light";
        }

        // update current theme in local storage
        setTheme(currentTheme);

        // remove old theme from ui
        document.querySelector("html").classList.remove(oldTheme);

        // add current theme to ui
        document.querySelector("html").classList.add(currentTheme);

        // change the text of button
        themeChangeButton.querySelector('span').textContent = currentTheme == "light" ? "Dark" : "Light";
    });
}