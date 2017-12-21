document.write("js読み込まれてる");

var World = {
    loaded: false,
    rotating: false,

    init: function initFn() {

        this.createModelAtLocation();
    },


    createModelAtLocation: function createModelAtLocationFn() {

        var location = new AR.RelativeLocation(null, 1, 1, 0);

        var modelMashu = new AR.Model("assets/cube.wt3", {
            onLoaded: this.worldLoaded,
            scale: {
                x: 0.001,
                y: 0.001,
                z: 0.001
            }
        });



        var obj = new AR.GeoObject(location, {
            drawables: {
                cam: [modelMashu]
            }
        });

    },

    worldLoaded: function worldLoadedFn() {
        World.loaded = true;
        var e = document.getElementById('loadingMessage');
        e.parentElement.removeChild(e);
    }

};

World.init();
