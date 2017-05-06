# Popular Movies - Changelog

## 1.0.0

First version, met all the criteria to pass the review (Stage #1).

## 1.0.1

Following up to some reviewer's suggestions, the following improvements have been made to the code:

 * `GridView` columns in the main screen adapt to device orientation
 * `FetchMoviesTask` has been moved in a separate class
 * Replaced `runOnUiThread()` call with `onPostExecute()` in `FetchMoviesTask`
 * Added image loading placeholders and fallback images to `ImageViews`
