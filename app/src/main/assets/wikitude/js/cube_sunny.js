var World = {
    loaded: false,
    rotating: false,

    init: function initFn() {
        this.createModelAtLocation();
    },

    //AR描画
    createModelAtLocation: function createModelAtLocationFn() {
        //モデルの定義
        var model = new AR.Model("assets/cube_sunny.wt3", {
            onLoaded: this.worldLoaded,
            //ARキャラの大きさ
            scale: {
                x: 0.001,
                y: 0.001,
                z: 0.001
            }
        });

        //ARを固定する
        this.tracker = new AR.InstantTracker({
            onChangeState: function onChangeStateFn(state) {
            },
            deviceHeight: 1.0
        });
        this.tracker.state = AR.InstantTrackerState.TRACKING;
        this.instantTrackable = new AR.InstantTrackable(this.tracker, {
            drawables: {
                cam: model
            }
        });
    },

    //ワールドの読み込み(AR表示前にやる処理)
    worldLoaded: function worldLoadedFn() {
        World.loaded = true;
    }
};

//実行
World.init();
