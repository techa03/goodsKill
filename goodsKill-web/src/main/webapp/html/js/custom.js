/*====================================
 Free To Use For Personal And Commercial Usage
Author: #
 License: Open source - MIT
 Please visit http://opensource.org/licenses/MIT for more Full Deatils of license.
 Share Us if You Like our work 
 Enjoy Our Codes For Free always.
======================================*/

(function ($) {
    "use strict";
    var mainApp = {

        main_fun: function () {
           
            var count = new countUp("error-link", 10, 404, 0, 0.3); //CHANGE 404 TO THE ERROR VALUE AS YOU WANT

            window.onload = function () {
                        count.start();
            }

            /*====================================
            WRITE YOUR SCRIPTS HERE
            ======================================*/
        },

        initialization: function () {
            mainApp.main_fun();

        }

    }
    // Initializing ///

    $(document).ready(function () {
        mainApp.main_fun();
    });

}(jQuery));
