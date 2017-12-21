document.write("aaaaaaaa");

var World = {
    loaded: false,
    rotating: false,

    init: function initFn() {
        document.write("ARcreate");

        this.createModelAtLocation();
    },


    createModelAtLocation: function createModelAtLocationFn() {

        var location = new AR.RelativeLocation(null, 1, 1, 0);


        document.write("AR読み込み");
        var modelMashu = new AR.Model("assets/cube.wt3", {
            onLoaded: this.worldLoaded,
            scale: {
                x: 0.001,
                y: 0.001,
                z: 0.001
            }
        });

        document.write("AR表示の前");


        var obj = new AR.GeoObject(location, {
            drawables: {
                cam: [modelMashu]
            }
        });


        document.write("AR表示のとこ");
    },

    worldLoaded: function worldLoadedFn() {
        World.loaded = true;
        var e = document.getElementById('loadingMessage');
        e.parentElement.removeChild(e);
    }

};

World.init();
