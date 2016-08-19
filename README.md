# comments-cloud


This is my "R days" project at Sciquest.

* This is a two part app:

  * dev/comments-cloud/builder 
      * used to generate comments data based on a targeted code base (currently configured to point at itself)

  * src/cljs/comments-cloud/core  
      * where the actual d3 code exists to render the generated data



## Overview

FIXME: Write a paragraph about the library/project and highlight its goals.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
