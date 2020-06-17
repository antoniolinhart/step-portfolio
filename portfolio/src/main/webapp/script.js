// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Generates a random color.
 * @returns {string} A color in HSL (hue, saturation, lightness) format
 */
function generateRandomColor() {
  const randomHue = Math.floor(Math.random() * 360); // max value 360
  const saturation =  Math.floor(Math.random() * 40) + 40 // max value 100
  const lightness = Math.floor(Math.random() * 30) + 60; // max value 100

  const randomHsl = `hsl(${randomHue}, ${saturation}%, ${lightness}%)`
  return randomHsl;
}

 /**
  * Changes the background of the page to be a random color.
  */
function setRandomBackgroundColor() {
  const bodyContainer = document.getElementsByTagName('body')[0];
  bodyContainer.style.backgroundColor = generateRandomColor();
}

/**
 * Call doGet to get content from the DataServlet.
 */
async function getComment() {
  const response = await fetch('/data');
  const comment = await response.json();
  document.getElementById('comment-container').innerHTML = comment;
}

/**
 * Fetches comments from the server and adds them to the DOM.
 */
async function loadComments() {
  const numComments = document.getElementById("num-comments").value;
  const deleteButtonContainer = document.getElementById("delete-comment-btn");

  const response = await fetch(`/list-comments?numComments=${numComments}`);
  const comments = await response.json();
  const commentContainerElement = document.getElementById('comment-container');
  const listElement = document.createElement('ul');
  listElement.className = 'comment-list';
  
  comments.forEach((comment) => {
    listElement.appendChild(createCommentElement(comment));
  })
  // Clear comments after each retrieval and replace with new
  commentContainerElement.innerHTML = '';
  commentContainerElement.appendChild(listElement);
  deleteButtonContainer.innerHTML = '';

  // If there are comments being displayed on the page, add a delete button
  if (comments.length > 0) {
    let deleteButton = document.createElement('button');
    deleteButton.onclick = deleteAllComments;
    deleteButton.innerText = "Delete Every Comment";
    deleteButtonContainer.appendChild(deleteButton); 
  }
}

/**
 * Makes a POST request to delete all comments.
 */
 function deleteAllComments() {
   fetch('/delete-comments', {method: 'POST'});
   window.location.reload();
 }

/**
 * Creates an element that represents a Comment.
 */
function createCommentElement(comment) {
  const totalCommentElement = document.createElement('li');

  const authorElement = document.createElement('span');
  authorElement.innerText = comment.authorName;
  authorElement.className = 'text-emphasis'

  const commentTextElement = document.createElement('p');
  commentTextElement.innerText = comment.commentText;

  totalCommentElement.appendChild(authorElement);
  totalCommentElement.appendChild(commentTextElement);
  return totalCommentElement;
}

/**
 * Initializes webpage.
 */
function init() {
  document.getElementById("background-button").addEventListener("click", setRandomBackgroundColor);
  document.getElementById("num-comments").onload = loadComments();
  document.getElementById("num-comments").setAttribute("onChange", "loadComments()");
}

init();