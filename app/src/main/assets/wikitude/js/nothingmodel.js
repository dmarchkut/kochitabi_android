document.write("cubeなし");

var World = {
    loaded: false,
    rotating: false,

    init: function initFn() {

        //this.createModelAtLocation();
    },


    createModelAtLocation: function createModelAtLocationFn() {
        //var location = new AR.RelativeLocation(null, -1, -1, 0);
        //var loc2 = new AR.GeoLocation(33.621323, 133.718946);  //krlab
        /*var altitude = loc2.altitude;


*/
        var modelMashu = new AR.Model("assets/cube.wt3", {
            onLoaded: this.worldLoaded,
            scale: {
                x: 0.002,
                y: 0.002,
                z: 0.002
            }

        });
/*
        var obj = new AR.GeoObject(loc2, {
            drawables: {
                cam: [modelMashu]
            }
        });
*/
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

    },

    worldLoaded: function worldLoadedFn() {
        World.loaded = true;
        var e = document.getElementById('loadingMessage');
        e.parentElement.removeChild(e);
    }
};

World.init();