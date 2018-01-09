document.write("js読み込まれてる");

var World = {
    loaded: false,
    rotating: false,

    init: function initFn() {
        this.createModelAtLocation();
    },


    createModelAtLocation: function createModelAtLocationFn() {
        //var location = new AR.RelativeLocation(null, -1, -1, 0);
        var loc2 = new AR.GeoLocation(33.621323, 133.718946);
        /*var altitude = loc2.altitude;


        var crossHairsRedImage = new AR.ImageResource("assets/crosshairs_red.png");
        var crossHairsRedDrawable = new AR.ImageDrawable(crossHairsRedImage, 1.0);

         var crossHairsBlueImage = new AR.ImageResource("assets/crosshairs_blue.png");
         var crossHairsBlueDrawable = new AR.ImageDrawable(crossHairsBlueImage, 1.0);
*/
        var modelMashu = new AR.Model("assets/cube.wt3", {
            onLoaded: this.worldLoaded,
            scale: {
                x: 0.001,
                y: 0.001,
                z: 0.001
            }

        });

        var obj = new AR.GeoObject(loc2, {
            drawables: {
                cam: [modelMashu]
            }
        });
/*
        this.tracker = new AR.InstantTracker({
            onChangeState: function onChangeStateFn(state) {
            }//,
            //deviceHeight: 1.0
        });
        this.tracker.state = AR.InstantTrackerState.TRACKING;
        this.instantTrackable = new AR.InstantTrackable(this.tracker, {
                    drawables: {
                        cam: modelMashu//,
                        //initialization: crossHairsRedDrawable
                    },
                                 onTrackingStarted: function onTrackingStartedFn() {
                                     // do something when tracking is started (recognized)
                                 }//,
                                 //onTrackingStopped: function onTrackingStoppedFn() {
                                     // do something when tracking is stopped (lost)
                                 //}
                                 //this.tracker.state = AR.InstantTrackerState.TRACKING;
        });
        //this.tracker.state = AR.InstantTrackerState.TRACKING;
*/
    },

  /*   changeTrackerState: function changeTrackerStateFn() {

         //if (this.tracker.state === AR.InstantTrackerState.INITIALIZING) {

             //document.getElementById("tracking-start-stop-button").src = "assets/stop.png";
             //document.getElementById("tracking-height-slider-container").style.visibility = "hidden";

             this.tracker.state = AR.InstantTrackerState.TRACKING;
         /*} else {

             document.getElementById("tracking-start-stop-button").src = "assets/start.png";
             document.getElementById("tracking-height-slider-container").style.visibility = "visible";

             this.tracker.state = AR.InstantTrackerState.INITIALIZING;
         }*/
   /*  },

     changeTrackingHeight: function changeTrackingHeightFn(height) {
         //this.tracker.deviceHeight = parseFloat(height);
     },

     isTracking: function isTrackingFn() {
         //return (this.tracker.state === AR.InstantTrackerState.TRACKING);
        return true;
     },
*/
    worldLoaded: function worldLoadedFn() {
        World.loaded = true;
        var e = document.getElementById('loadingMessage');
        e.parentElement.removeChild(e);
    }
};

World.init();
