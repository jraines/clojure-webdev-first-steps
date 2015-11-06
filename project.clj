(defproject simoutfit "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [compojure "1.4.0"]
                 [http-kit "2.1.19"]
                 [ring/ring-core "1.4.0"]
                 [environ "1.0.1"]
                 [ring-transit "0.1.4"]
                 [org.omcljs/om "1.0.0-alpha17"]
                 [com.cognitect/transit-clj "0.8.285"]
                 [com.cognitect/transit-cljs "0.8.225"]
                 [yesql "0.5.1"]
                 [ragtime "0.5.2"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

  :plugins [[lein-environ "1.0.1"]
            [lein-cljsbuild "1.1.1-SNAPSHOT"]
            [lein-figwheel "0.5.0-SNAPSHOT"]]

  :source-paths ["src" "dev"]


  :aliases {"migrate"  ["run" "-m" "user/migrate"]
            "rollback" ["run" "-m" "user/rollback"]}

  :profiles {
             :dev  {:env {:environment "dev"}
                    :dependencies [[ring/ring-devel "1.4.0"]
                                   [figwheel-sidecar "0.5.0-SNAPSHOT"]
                                   [figwheel "0.5.0-SNAPSHOT"]
                                   [devcards "0.2.0-8"]
                                   [com.cemerick/piggieback "0.2.1"]
                                   [org.clojure/tools.nrepl "0.2.12"]]
                    :plugins [[mvxcvi/whidbey "1.3.0"]]
                    :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

             :uberjar {
                       ;this runs the first entry in the cljsbuils :builds map below
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:environment "production"}
                       :main simoutfit.server
                       :aot :all
                       :source-paths ["src"]
                       :omit-source true
                       }}

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
    :builds [
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/simoutfit.js"
                         :main simoutfit.core
                         :optimizations :advanced
                         :pretty-print false}}

             {:id "dev"
              :source-paths ["src" "dev"]

              :figwheel { :on-jsload "simoutfit.core/on-js-reload" }

              :compiler {:main simoutfit.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/simoutfit.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}

             {:id "devcards"
              :source-paths ["src" "dev"]
              :figwheel { :devcards true } ;; <- note this
              :compiler { :main    simoutfit.cards
                         :asset-path "js/compiled/devcards_out"
                         :output-to  "resources/public/js/compiled/simoutfit_devcards.js"
                         :output-dir "resources/public/js/compiled/devcards_out"
                         :source-map-timestamp true }}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             :nrepl-port 7888

             ;; :ring-handler hello_world.server/handler

             ;; :open-file-command "myfile-opener"

             ;; :repl false

             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             })
