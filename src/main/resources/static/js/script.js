const wordDictionary = {
    "array": "A data structure that holds a collection of elements, accessible by index.",
    "class": "A blueprint for creating objects in object-oriented programming.",
    "input": "Data that is provided to a program by the user or another system.",
    "logic": "A set of rules used to make decisions or control flow in a program.",
    "model": "A representation of data or logic, often used in MVC architecture.",
    "fetch": "A method for retrieving data, commonly used for making HTTP requests.",
    "merge": "To combine multiple sources or branches of code or data into one.",
    "stack": "A LIFO (Last-In-First-Out) data structure used in memory management and algorithms.",
    "error": "An issue or bug that prevents code from running as expected.",
    "token": "A piece of data representing authentication or lexical elements in parsing.",
    "query": "A request to retrieve or manipulate data, usually from a database.",
    "cache": "A temporary storage to speed up repeated access to data.",
    "proxy": "An intermediary server or object that acts on behalf of another.",
    "reset": "To return a system or variable back to its initial state.",
    "trace": "To follow and examine the execution flow of a program, especially in debugging.",
    "scope": "Defines where variables or functions are accessible in code.",
    "shift": "An array operation that removes the first element.",
    "click": "A common UI event triggered by mouse or pointer interaction.",
    "style": "Defines the appearance of UI elements, usually with CSS.",
    "value": "The data assigned to a variable or element.",
    "media": "Refers to files like images, videos, or audio used in applications.",
    "index": "A numerical representation of position in arrays or databases.",
    "table": "A structured set of data organized in rows and columns.",
    "flush": "To force writing of data from buffer to destination (like memory or disk).",
    "route": "A path that maps URLs to specific code/functions in web frameworks.",
    "click": "An event handler for user interface interaction, such as button presses.",
    "cover": "Used in CSS or layouts to describe background scaling behavior.",
    "hover": "A UI state triggered when a cursor is placed over an element.",
    "block": "A container or grouping element in programming or layout systems.",
    "focus": "An element’s active or selected state, especially for inputs."
};



const letters = document.querySelectorAll(".letter-box");
const loadingDiv = document.querySelector(".loading");
const brandName = document.querySelector(".brand-name");
const ANSWER_LENGTH = 5;
let done = false;
let isLoading = false;
let currentGuess = "";
let currentRow = 0;
let remainingLife = 5;



async function init() {

    const today = getTodaysWord();
    let CORRECT_WORD = today.word.toUpperCase();
    let map = makeMap(CORRECT_WORD.toUpperCase());
    console.log(CORRECT_WORD);
    console.log(today.definition);

    document.querySelector(".keyboard").addEventListener("click", function (event) {
    if (done || isLoading) {
        return;
    }

    const inputLetter = event.target.textContent?.trim().toUpperCase();
    if (!inputLetter) return;

    if (isLetter(inputLetter)) {
        addLetter(inputLetter);
    }
    else if (inputLetter === "ENTER") {
        if (currentGuess.length === ANSWER_LENGTH) {
            commit(CORRECT_WORD, map);
        }
    }
    else if (inputLetter === "⌫") {
        erase();
    }
    else {
        //Do nothing
    }
});



    document.addEventListener("keydown", function (event) {
        if (done || isLoading) {
            //Do nothing
            return;
        }
        inputLetter = event.key.toUpperCase();
        if (isLetter(inputLetter)) {
            addLetter(inputLetter);
        }
        else if (inputLetter === "ENTER") {

            if (currentGuess.length === ANSWER_LENGTH) {
                commit(CORRECT_WORD, map);
            }


        }
        else if (inputLetter === "BACKSPACE") {
            erase();
        }
        else {
            //Do nothing
        }

    });

        document.querySelector(".hint-button").addEventListener("click", () => {
        const popup = document.querySelector(".hint-popup");
        const hintText = document.querySelector(".hint-text");
        hintText.textContent = today.definition;
        popup.classList.remove("hidden");
    });

    document.querySelector(".hint-ok-button").addEventListener("click", () => {
        document.querySelector(".hint-popup").classList.add("hidden");
    });

        const burger = document.querySelector(".burger");
    const fullscreenMenu = document.querySelector(".fullscreen-menu");
    const closeBtn = document.querySelector(".close-menu");

    burger.addEventListener("click", () => {
        fullscreenMenu.classList.remove("hidden");
        fullscreenMenu.classList.add("show");
    });

    closeBtn.addEventListener("click", () => {
        fullscreenMenu.classList.remove("show");
        setTimeout(() => {
            fullscreenMenu.classList.add("hidden");
        }, 300); // transition süresi kadar
    });

    
};

init();

    

// Zoom'u engelle ama hızlı yazmayı bozmadan
(function () {
  const isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
  let lastTouchTime = 0;

  // Sadece Chrome/diğer tarayıcılar için touchend'i kullan (Safari tıklamayı yutuyor)
  if (!isSafari) {
    document.body.addEventListener('touchend', function (event) {
      const now = new Date().getTime();
      const timeDiff = now - lastTouchTime;

      if (timeDiff < 200 && event.cancelable) {
        event.preventDefault();
      }

      lastTouchTime = now;
    }, { passive: false });
  }

  // Pinch-to-zoom gesture engelle (her tarayıcı için güvenli)
  document.body.addEventListener('gesturestart', function (e) {
    if (e.cancelable) e.preventDefault();
  }, { passive: false });

  // Çift tıklamayla zoom'u da engelle (her tarayıcı için güvenli)
  document.body.addEventListener('dblclick', function (e) {
    if (e.cancelable) e.preventDefault();
  }, { passive: false });
})();





function getTodaysWord() {
    const now = new Date();
    const turkeyOffset = 3 * 60;
    const turkeyDate = new Date(now.getTime() + turkeyOffset * 60000);

    const startOfDay = new Date(Date.UTC(1970, 0, 1));
    const daysSinceStart = Math.floor((turkeyDate - startOfDay) / (1000 * 60 * 60 * 24));

    const keys = Object.keys(wordDictionary);
    const index = daysSinceStart % keys.length;
    const word = keys[index];
    const definition = wordDictionary[word];

    return { word, definition };
}

function isLetter(letter) {
    return /^[a-zA-Z]$/.test(letter);
}

function addLetter(letter) {
    if (currentGuess.length < ANSWER_LENGTH) {
        letters[currentRow * ANSWER_LENGTH + currentGuess.length].innerText = letter;
        currentGuess += letter;
    }
    else if (currentGuess.length === ANSWER_LENGTH) {
        letters[currentRow * ANSWER_LENGTH + currentGuess.length - 1].innerText = letter;
        currentGuess = currentGuess.substring(0, currentGuess.length - 1) + letter;
    }

}
function erase() {
    if (currentGuess.length > 0) {
        letters[currentRow * ANSWER_LENGTH + (currentGuess.length - 1)].innerText = "";
        currentGuess = currentGuess.substring(0, currentGuess.length - 1);
    }
    else {
        //Do nothing
    }
}

async function validateWord(currentGuess) {

}



async function commit(CORRECT_WORD, map) {

    //TODO check commit function.

    loading(true);
    const response = await fetch("https://words.dev-apis.com/validate-word", {
        method: "POST",
        body: JSON.stringify(
            {
                "word": currentGuess
            }
        )
    })
    loading(false);
    const responsedData = await response.json();
    const isItValid = responsedData.validWord;
    console.log(isItValid);


    if (isItValid && currentGuess.length === ANSWER_LENGTH) {
        for (let i = 0; i < ANSWER_LENGTH; i++) {
            if (currentGuess[i] === CORRECT_WORD[i]) {
                letters[currentRow * ANSWER_LENGTH + i].classList.add("correct");
                map[CORRECT_WORD[i]]--;
            }

        }
        for (let i = 0; i < ANSWER_LENGTH; i++) {
            if (currentGuess[i] === CORRECT_WORD[i]) {
            }
            else if (CORRECT_WORD.includes(currentGuess[i]) && map[currentGuess[i]] != 0) {
                letters[currentRow * ANSWER_LENGTH + i].classList.add("close");
                map[CORRECT_WORD[i]]--;
                console.log(map);
            }
            else {
                letters[currentRow * ANSWER_LENGTH + i].classList.add("wrong");
                console.log(map);
            }
        }
    }
    else if (!isItValid && currentGuess.length === ANSWER_LENGTH) {
        for (let i = 0; i < ANSWER_LENGTH; i++) {
            letters[currentRow * ANSWER_LENGTH + i].classList.add("invalid");

        }
        for (let i = 0; i < ANSWER_LENGTH; i++) {
            setTimeout(function () {
                letters[currentRow * ANSWER_LENGTH + i].classList.remove("invalid");
            }, 1000);
        }
    }
    else {
        //Do nothing.
    }
    if (currentGuess.length === ANSWER_LENGTH && currentGuess != CORRECT_WORD && remainingLife >= 1 && isItValid) {
        currentRow++;
        remainingLife--;
        currentGuess = "";
        return;
    }
    else if (currentGuess === CORRECT_WORD) {

        winner();
        done = true;
        return;
    }
    else if (currentGuess.length === ANSWER_LENGTH && currentGuess != CORRECT_WORD && remainingLife === 0 && isItValid) {
        loser();
        done = true;
        return;
    }
    else {
        //Do nothing.
    }

}

function loading(status) {
    loadingDiv.classList.toggle("hidden");
    if (status === true) {
        isLoading = true;
        return isLoading;
    }
    else if (status === false) {
        isLoading = false;
        return isLoading;
    }

}

function winner() {
    brandName.classList.add("winner");
}

function loser() {
    brandName.classList.add("loser");
}




function makeMap(array) {
    const obj = {
    }
    for (let i = 0; i < array.length; i++) {
        const letter = array[i]
        if (obj[letter]) {
            obj[letter]++
        }
        else {
            obj[letter] = 1;
        }
    }
    return obj;
}