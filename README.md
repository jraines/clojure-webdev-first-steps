# Gentle Intro to Web Development in Clojure

## Coverage goals

- Develoment environment setup (lein-figwheel, a REPL, (a little) Emacs
- Minimal server setup
- Om on the client side
- Devcards for interactive UI development
- Database interaction with YeSQL
- Deploying to a server with Ansible

*But first,*

## Assumptions, Caveats, and who this is for

I assume you know Clojure syntax, and are familiar with (but far from an expert on) the Clojure & ClojureScript ecosystem. Maybe you're like me, and have been dabbling for a while -- doing some 4Clojure problems, reading a book or two, maybe putting together a small project, but pretty sure you were Doing It Wrong.

If that sounds like you, unfortunately this guide won't solve that, but I hope it will be a step forward towards the goal of *putting it all together*.  I aim to cover some environment setup, basic setup of a server, and make a few pieces of the exciting potential of Om Next a bit more approachable. Finally we'll deploy to a server.

## Starting with Leiningen and Figwheel

To create a new project, run `lein new figwheel appname`

Then to start the browser repl: `lein figwheel`

For a nicer repl (arrow key history navigation & more), you'll probably want to install `rlwrap` (available via Homebrew on OSX), then start the repl with `rlwrap lein figwheel`

In `src/[appname]/core.cljs` You can edit the `on-js-reload` function to do something every time the code is hot reloaded.

### Running the repl from Emacs with Cider

In `project.clj` uncomment `:nrepl-port 7888` in the `:figwheel` section of the config.

To ensure that Cider uses the correct version of nREPL tools, add this to `project.clj`:

```clojure
:profiles {:dev { :dependencies [[org.clojure/tools.nrepl "0.2.12"]] }
           :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-repl]} }
```

In terminal, run `lein figwheel`. You should see the figwheel server start, awaiting a browser connection.

Then, in Emacs, do `cider-connect`, choose `localhost` then manually enter `7888` and press enter.

You may have to then manually open the cider REPL buffer (I think this is a bug, perhaps local to my Emacs version/installation, and it's supposed to open on connect).

Once the REPL is available, enter `(use 'figwheel-sidecar.repl-api)` then `(cljs-repl)`.   Now when you load your `index.html` in the browser, you should get your `cljs.user>` browser repl.  Executing `(println "Hi")` should make that message show up in your page's javascript console.

I skip that `(use 'figwheel-sidecar.repl-api)` by adding the following to a `dev/user.clj` file and telling Leiningen to use that by adding `"dev"` to the `:source-paths` vector in `project.clj`.

```clojure
 (ns user
  (:require [figwheel-sidecar.repl-api :refer [cljs-repl]]))
```

You can place other customizations for your dev environment REPL here.

###CSS watching / reloading

CSS files inside `resources/public/css` will automatically be reloaded.
You can customize this in the `:figwheel` map of options in the `:cljsbuild` options map in `project.clj`

## A server with Ring, Compojure, and httpkit

Add the following dependencies to `project.clj`

```clojure
[compojure "1.4.0"]                     ;routing
[http-kit "2.1.19"]                     ;server 
[com.cognitect/transit-clj "0.8.285"]   ;transit for clj
[com.cognitect/transit-cljs "0.8.225"]  ;transit for cljs
```

We'll use compojure to define the routes our application will respond to and http-kit to be our web server.

Transit is a data format that plays extra nicely with clojure data structures.

[In a new file](https://github.com/jraines/gentle-om-next/commit/8e70c146ee3e543f90b7476a202f06a7fd65c354#diff-59ac2781f662f112526300f4a4719b87R1), we set up our server, routes, and add a few helper functions for writing & reading transit data.

Don't be too put off by the boilerplate around reading and writing transit; I copied it from [here](https://github.com/swannodette/transit-example) and there's likely a better way (including the ring-transit middleware we'll see shortly).

Finally we use [a library](https://github.com/jraines/gentle-om-next/commit/8e70c146ee3e543f90b7476a202f06a7fd65c354#diff-51041914672e7e8c6288e92ec0a1d56fR3) from Google Closure to make an ajax request to fetch some data.


## Om

The next few commits follow the [Om Next quickstart guide](https://github.com/omcljs/om/wiki/Quick-Start-(om.next)), so I won't dwell much on them. (In fact, if this is your first time checking out Om, you should definitely go there first -- I really only want to spend time on the piece of Om that I found confusing -- the "boring" mechanics of setting up client / server sync.  It's actually quite easy, just not currently well documented, in my opinion. *Note:  as of this writing, the version of Om I'm talking about is currently called Om Next, and is in alpha status, and thus so is this part of the guide*).

[First we'll add Om as a dependency and make a basic component.](https://github.com/jraines/gentle-om-next/commit/ab0150afafdfe3b305270d6a8e8977f7fedf8985)

[Then, let's make it accept props as parameters.](https://github.com/jraines/gentle-om-next/commit/39be499a06b89698fe2048f4f8dcfc905a0b4400)

[Next, let's set up our read function, parser, and reconciler, and grab data from the server](https://github.com/jraines/gentle-om-next/commit/f44d51670c992fbfc909b324e5541e4b68e991da)

At this point you should see that your component:

1. Expresses its data requirements as an Om query expression
2. Is provided that data from the app state by the reconciler, which uses the parser that you defined to process the query.
3. Can (via the read function you give to the parser) request that all or part of its data requirements be fetch from the server if not found locally.

Now let's let Om manage our state between client and server instead of handling that ourselves with explicit ajax requests and `swap!`ing the `app-state`.

We'll need to provide a function to the `:send` key of the reconciler's parameter map.  This will be a function which takes two parameters:  the EDN of the query expression fragment that will be passed to the server, and a callback to handle the response. In this case, our function closes over the single remote URL we'll be sending to on the server.  Notice that what we *don't* have to do in the callback is update our app-state:  Om handles it.

On the server, we have a similar `om/parser` function which takes the app state and a `:read` function.

In the request handler, we respond with a transit-encoded result of parsing the state and the query expression fragment that was sent, which is nested in the `:remote` key of the transit encoded params (these are decoded for us by ring-transit).  This is where using transit pays off -- we can pass this piece of the request directly into the parser function (note - this is the function *created* by `om/parser` as parameterized with your read (and mutate, which I don't cover here) functions).

In the case of `:description`, the value is sent back to the server, but since there is no `:sender` key in the app state on the server, we send back `:not-found` and let the client handle that.

I also added `ring-reload` so I didn't have to restart the server on each change.

[Here's the commit for the above setup](https://github.com/jraines/gentle-om-next/commit/9e92e22307db3e3086a7b5404b78424625ad6407)

##Devcards

[Devcards](https://github.com/bhauman/devcards) is a tool that allows for interactive development of UI components in isolations. You can view them at different states at the same time, compare different edge cases (long text, blank values, etc) without constantly having to replicate that in your main app.  It has a host of other capabilities as well.

It has great documentation, so I'll refer you there, and to [this commit](https://github.com/jraines/gentle-om-next/commit/77abe9a72b556b86890bd1a8160694a011625d4a) that got me set up in the most basic way.  Note that to get real benefits, you'll need to do some setup and/or mocking of your Om parsers, reconciler, and possibly server so you can test your components in all their states.

The one thing I missed, due to a lack of understanding about Leiningen & cljsbuild, was that I needed to do `lein figwheel devcards` to start it up, rather than `lein figwheel`, which uses your `:dev` build profile.

#Working with a SQL database

##YesQL

[YesQL](https://github.com/krisajenkins/yesql) is a simple way to use SQL from Clojure, no extra DSL on top of it.  You put your queries in their own `.sql` files, and then generate functions for run them with the `defquery` macro.

This one is pretty straigtforward, so you can go straight from the docs, with two caveats, one borne of my lack of mastery of Leiningen environments and one which I think is an omission from the docs (even if in this case, too, I may be missing some knowledge about SQL files):

1. If you put your query files in `resources`, you don't include that in your reference to the SQL file in `defquery`, because `resources` is part of the Leiningen classpath.
2. The SQL files must start with a blank line.  -_-

##Ragtime

Coming from Rails, I can't live without database migrations.  [Ragtime](https://github.com/weavejester/ragtime) gives us this ability. I used the suggested [Leiningen integration](https://github.com/weavejester/ragtime/wiki/Leiningen-Integration), and the trickiest thing I found about this, was if I had an error in my SQL, no error would be thrown from `lein migrate`.  The table just wouldn't be there. I need to look into it more and see if you just have to rescue exceptions manually and print something on migration failure.

Note: the same point as the previous section about referencing paths to your migrations directory within `resources` applies here.

Also, from the docs: "Note that alphanumeric ordering is used, so if a single migration has more than 9 parts, then start off with 01, 02, etc"

I think I need more exploration in the database area.  I'm not really satisifed with this combo yet, but it's definitely enough to write a simple app backed by a SQL database. I have a feeling I'm going to miss ActiveRecord, though, for all the shit it gets in the Ruby community.

#Deploying to a VPS with Ansible

The goal is to have a one line task that will package up our app, provision a server with everything it needs, prep our database, and run our app using `java -jar /path/to/jar`

## Building the uberjar

You'll want to ensure that your development dependencies don't get shipped with your production app.  I found the [environ](https://github.com/weavejester/environ) tool useful for this, and also shuffled a few dependencies from the main list in `project.clj` to specific `:profiles` sections.

(I had to do a [bit of hackery](https://github.com/jraines/gentle-om-next/commit/ea94287bb9acf6057273ae6db5c58ac6d06cb4b2#diff-59ac2781f662f112526300f4a4719b87R12) to ensure my project could be built for production without its development dependencies, despite the fact that references to development-only functions were contained in `defn`'d fuctions that wouldn't be called in production mode. Would love feedback on the right way to handle this.

In your main class that will run your server, add `(:gen-class)`

In the `:uberjar` profile in `project.clj`, this line will ensure that your production js gets included:

```clojure
 :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
```

See [this discussion](https://github.com/emezeske/lein-cljsbuild/issues/366#issuecomment-134230350) for using this method over the Leiningen hook for cljsbuild.

Issue: I see multiple "Compiling [ns]..." outputs for each namespace, despite no circular dependencies.  I'm not sure if this is a sign of something wrong, but everything seems to work.

## Provisioning with Ansible

I did this on a DigitalOcean VPS running Ubuntu 15.10.  There will be differnces compared to older Ubuntu versions.

One particular one is that Ansible expects Python 2.x, and Ubuntu 15.10 ships with Python 3 as its default.  So step 0 is to log into the server and run: 
`apt-get install python-simplejson`

Now you can create an Ansible playbook to provision the server.  This is beyond the scope of this README for the most part, but here's a few notes just of what I learned. (TODO - still in progress)

I also wrote about doing so [in this blog post](http://jeremyraines.com/2014/09/13/deploying-a-microservice-with-ansible.html).

Need to check in on the db?
`psql myapp -U myappuser -h 127.0.0.1 --password`

Organization of ansible roles
http://docs.ansible.com/ansible/playbooks_best_practices.html#task-and-handler-organization-for-a-role

Some helpful steps for setting java & lein with Ansible
https://semaphoreci.com/community/tutorials/how-to-set-up-a-clojure-environment-with-ansible



