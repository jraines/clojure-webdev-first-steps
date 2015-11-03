# simoutfit

## Starting

Run `lein new figwheel appname`

Then to start the browser repl: `lein figwheel`

For a nicer repl (arrow key history navigation & more), you'll probably want to install `rlwrap` (available via Homebrew on OSX), then start the repl with `rlwrap lein figwheel`

You can edit the `on-js-reload` function to do something every time
the code is hot reloaded.  Later we'll see how to ensure edits in
other namespaces can trigger hot reloading.

## Running the repl from Emacs with Cider

In `project.clj` uncomment `:nrepl-port 7888` in the `:figwheel` section of the config.

To ensure that Cider uses the correct version of nREPL tools, add this to `project.clj`:

```clojure
:profiles {:dev { :dependencies [[org.clojure/tools.nrepl "0.2.12"]] }
           :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-repl]} }
```

In terminal, run `lein figwheel`. You should see the figwheel server start, awaiting a browser connection.

Then, in Emacs, do `cider-connect`, choose `localhost` then manually enter `7888` and press enter.

You may have to then open the buffer with the repl (I think this is a bug and it's supposed to open on connect).

Once the REPL is available, enter `(use `figwheel-sidecar.repl-api`)` then `(cljs-repl)`.   Now when you load your `index.html` in the browser, you should get your `cljs.user>` browser repl.  Executing `(println "Hi")` should make that message show up in your page's javascript console.

I skip that first expression by adding the following to a `dev/user.clj` file and telling Leiningen to use that by adding `"dev"` to the `:source-paths` vector in `project.clj`.

```clojure
 (ns user
  (:require [figwheel-sidecar.repl-api :refer [cljs-repl]]))
```

##CSS watching / reloading

CSS files inside `resources/public/css` will automatically be reloaded.
You can customize this in the `:figwheel` map of options in the `:cljsbuild` options map in `project.clj`

### Let's fetch some data from a server

Add the following dependencies to `project.clj`

```clojure
[compojure "1.4.0"]                     ;routing
[http-kit "2.1.19"]                     ;server 
[com.cognitect/transit-clj "0.8.285"]   ;transit for clj
[com.cognitect/transit-cljs "0.8.225"]  ;transit for cljs
```

We'll use compojure to define the routes our application will respond to and http-kit to be our web server.

Transit is a data format that plays extra nicely with clojure data structures.

In this commit, we set up our server, routes, and add a few helper functions
for writing & reading transit data.  Finally we use a library from Google Closure to make an ajax request to fetch some data.


### Om

The next few commits follow the Om quickstart guide

