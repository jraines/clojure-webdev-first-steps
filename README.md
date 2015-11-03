# simoutfit

## Starting

Run `lein new figwheel appname`

Then to start the browser repl: `lein figwheel`

For a nicer repl (arrow key history navigation & more), you'll probably want to install `rlwrap` (available via Homebrew on OSX), then start the repl with `rlwrap lein figwheel`

You can edit the `on-js-reload` function to do something every time
the code is hot reloaded.  Later we'll see how to ensure edits in
other namespaces can trigger hot reloading.

## Running the repl from Emacs with Cider


