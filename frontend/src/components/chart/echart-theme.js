import * as echarts from 'echarts';

export const echartsThemes = {
    customed: {
        name: '默认',
        options: {
            "color": [
                "#5470c6",
                "#91cc75",
                "#fac858",
                "#ee6666",
                "#73c0de",
                "#3ba272",
                "#fc8452",
                "#9a60b4",
                "#ea7ccc"
            ],
            "backgroundColor": "rgba(0,0,0,0)",
            "textStyle": {},
            "title": {
                "textStyle": {
                    "color": "#464646"
                },
                "subtextStyle": {
                    "color": "#6e7079"
                }
            },
            "line": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false
            },
            "radar": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false
            },
            "bar": {
                "itemStyle": {
                    "barBorderWidth": 0,
                    "barBorderColor": "#ccc"
                }
            },
            "pie": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "scatter": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "boxplot": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "parallel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "sankey": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "funnel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "gauge": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "candlestick": {
                "itemStyle": {
                    "color": "#eb5454",
                    "color0": "#47b262",
                    "borderColor": "#eb5454",
                    "borderColor0": "#47b262",
                    "borderWidth": 1
                }
            },
            "graph": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                },
                "lineStyle": {
                    "width": 1,
                    "color": "#aaaaaa"
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false,
                "color": [
                    "#5470c6",
                    "#91cc75",
                    "#fac858",
                    "#ee6666",
                    "#73c0de",
                    "#3ba272",
                    "#fc8452",
                    "#9a60b4",
                    "#ea7ccc"
                ],
                "label": {
                    "color": "#eeeeee"
                }
            },
            "map": {
                "itemStyle": {
                    "areaColor": "#eee",
                    "borderColor": "#444",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#000"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(255,215,0,0.8)",
                        "borderColor": "#444",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(100,0,0)"
                    }
                }
            },
            "geo": {
                "itemStyle": {
                    "areaColor": "#eee",
                    "borderColor": "#444",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#000"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(255,215,0,0.8)",
                        "borderColor": "#444",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(100,0,0)"
                    }
                }
            },
            "categoryAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#6E7079"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#E0E6F1"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.2)",
                            "rgba(210,219,238,0.2)"
                        ]
                    }
                }
            },
            "valueAxis": {
                "axisLine": {
                    "show": false,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#6E7079"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#E0E6F1"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.2)",
                            "rgba(210,219,238,0.2)"
                        ]
                    }
                }
            },
            "logAxis": {
                "axisLine": {
                    "show": false,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#6E7079"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#E0E6F1"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.2)",
                            "rgba(210,219,238,0.2)"
                        ]
                    }
                }
            },
            "timeAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#6E7079"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#6E7079"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#E0E6F1"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.2)",
                            "rgba(210,219,238,0.2)"
                        ]
                    }
                }
            },
            "toolbox": {
                "iconStyle": {
                    "borderColor": "#999999"
                },
                "emphasis": {
                    "iconStyle": {
                        "borderColor": "#666666"
                    }
                }
            },
            "legend": {
                "textStyle": {
                    "color": "#333333"
                }
            },
            "tooltip": {
                "axisPointer": {
                    "lineStyle": {
                        "color": "#cccccc",
                        "width": 1
                    },
                    "crossStyle": {
                        "color": "#cccccc",
                        "width": 1
                    }
                }
            },
            "timeline": {
                "lineStyle": {
                    "color": "#dae1f5",
                    "width": 2
                },
                "itemStyle": {
                    "color": "#a4b1d7",
                    "borderWidth": 1
                },
                "controlStyle": {
                    "color": "#a4b1d7",
                    "borderColor": "#a4b1d7",
                    "borderWidth": 1
                },
                "checkpointStyle": {
                    "color": "#316bf3",
                    "borderColor": "#ffffff"
                },
                "label": {
                    "color": "#a4b1d7"
                },
                "emphasis": {
                    "itemStyle": {
                        "color": "#ffffff"
                    },
                    "controlStyle": {
                        "color": "#a4b1d7",
                        "borderColor": "#a4b1d7",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "#a4b1d7"
                    }
                }
            },
            "visualMap": {
                "color": [
                    "#bf444c",
                    "#d88273",
                    "#f6efa6"
                ]
            },
            "dataZoom": {
                "handleSize": "undefined%",
                "textStyle": {}
            },
            "markPoint": {
                "label": {
                    "color": "#eeeeee"
                },
                "emphasis": {
                    "label": {
                        "color": "#eeeeee"
                    }
                }
            }
        }
    },
    dark: {
        name: '深色',
        options: {
            "color":
                [
                    "#dd6b66",
                    "#759aa0",
                    "#e69d87",
                    "#8dc1a9",
                    "#ea7e53",
                    "#eedd78",
                    "#73a373",
                    "#73b9bc",
                    "#7289ab",
                    "#91ca8c",
                    "#f49f42"
                ],
            "backgroundColor":
                "#282a36",
            "textStyle":
                {
                    "color": "#e0dddd"
                }
            ,
            "title":
                {
                    "textStyle":
                        {
                            "color":
                                "#eeeeee"
                        }
                    ,
                    "subtextStyle":
                        {
                            "color":
                                "#aaaaaa"
                        }
                }
            ,
            "line":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                1
                        }
                    ,
                    "lineStyle":
                        {
                            "width":
                                2
                        }
                    ,
                    "symbolSize":
                        4,
                    "symbol":
                        "circle",
                    "smooth":
                        false
                }
            ,
            "radar":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                1
                        }
                    ,
                    "lineStyle":
                        {
                            "width":
                                2
                        }
                    ,
                    "symbolSize":
                        4,
                    "symbol":
                        "circle",
                    "smooth":
                        false
                }
            ,
            "bar":
                {
                    "itemStyle":
                        {
                            "barBorderWidth":
                                0,
                            "barBorderColor":
                                "#ccc"
                        }
                }
            ,
            "pie":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        }
                }
            ,
            "scatter":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        }
                }
            ,
            "boxplot":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        }
                }
            ,
            "parallel":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        }
                }
            ,
            "sankey":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        }
                }
            ,
            "funnel":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        }
                }
            ,
            "gauge":
                {
                    textStyle: {
                        color: '#e0dddd'
                    },
                    title: {
                        color: '#e0dddd'
                    },
                    axisLabel: {
                        color: '#e0dddd'
                    },
                    itemStyle:
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        },
                    detail: {
                        color: '#e0dddd'
                    },
                }
            ,
            "candlestick":
                {
                    "itemStyle":
                        {
                            "color":
                                "#fd1050",
                            "color0":
                                "#0cf49b",
                            "borderColor":
                                "#fd1050",
                            "borderColor0":
                                "#0cf49b",
                            "borderWidth":
                                1
                        }
                }
            ,
            "graph":
                {
                    "itemStyle":
                        {
                            "borderWidth":
                                0,
                            "borderColor":
                                "#ccc"
                        }
                    ,
                    "lineStyle":
                        {
                            "width":
                                1,
                            "color":
                                "#aaaaaa"
                        }
                    ,
                    "symbolSize":
                        4,
                    "symbol":
                        "circle",
                    "smooth":
                        false,
                    "color":
                        [
                            "#dd6b66",
                            "#759aa0",
                            "#e69d87",
                            "#8dc1a9",
                            "#ea7e53",
                            "#eedd78",
                            "#73a373",
                            "#73b9bc",
                            "#7289ab",
                            "#91ca8c",
                            "#f49f42"
                        ],
                    "label":
                        {
                            "color":
                                "#eeeeee"
                        }
                }
            ,
            "map":
                {
                    "itemStyle":
                        {
                            "areaColor":
                                "#eee",
                            "borderColor":
                                "#444",
                            "borderWidth":
                                0.5
                        }
                    ,
                    "label":
                        {
                            "color":
                                "#000"
                        }
                    ,
                    "emphasis":
                        {
                            "itemStyle":
                                {
                                    "areaColor":
                                        "rgba(255,215,0,0.8)",
                                    "borderColor":
                                        "#444",
                                    "borderWidth":
                                        1
                                }
                            ,
                            "label":
                                {
                                    "color":
                                        "rgb(100,0,0)"
                                }
                        }
                }
            ,
            "geo":
                {
                    "itemStyle":
                        {
                            "areaColor":
                                "#eee",
                            "borderColor":
                                "#444",
                            "borderWidth":
                                0.5
                        }
                    ,
                    "label":
                        {
                            "color":
                                "#000"
                        }
                    ,
                    "emphasis":
                        {
                            "itemStyle":
                                {
                                    "areaColor":
                                        "rgba(255,215,0,0.8)",
                                    "borderColor":
                                        "#444",
                                    "borderWidth":
                                        1
                                }
                            ,
                            "label":
                                {
                                    "color":
                                        "rgb(100,0,0)"
                                }
                        }
                }
            ,
            "categoryAxis":
                {
                    "axisLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisTick":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisLabel":
                        {
                            "show":
                                true,
                            "color":
                                "#eeeeee"
                        }
                    ,
                    "splitLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        [
                                            "#aaaaaa"
                                        ]
                                }
                        }
                    ,
                    "splitArea":
                        {
                            "show":
                                false,
                            "areaStyle":
                                {
                                    "color":
                                        [
                                            "#eeeeee"
                                        ]
                                }
                        }
                }
            ,
            "valueAxis":
                {
                    "axisLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisTick":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisLabel":
                        {
                            "show":
                                true,
                            "color":
                                "#eeeeee"
                        }
                    ,
                    "splitLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        [
                                            "#aaaaaa"
                                        ]
                                }
                        }
                    ,
                    "splitArea":
                        {
                            "show":
                                false,
                            "areaStyle":
                                {
                                    "color":
                                        [
                                            "#eeeeee"
                                        ]
                                }
                        }
                }
            ,
            "logAxis":
                {
                    "axisLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisTick":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisLabel":
                        {
                            "show":
                                true,
                            "color":
                                "#eeeeee"
                        }
                    ,
                    "splitLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        [
                                            "#aaaaaa"
                                        ]
                                }
                        }
                    ,
                    "splitArea":
                        {
                            "show":
                                false,
                            "areaStyle":
                                {
                                    "color":
                                        [
                                            "#eeeeee"
                                        ]
                                }
                        }
                }
            ,
            "timeAxis":
                {
                    "axisLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisTick":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                    ,
                    "axisLabel":
                        {
                            "show":
                                true,
                            "color":
                                "#eeeeee"
                        }
                    ,
                    "splitLine":
                        {
                            "show":
                                true,
                            "lineStyle":
                                {
                                    "color":
                                        [
                                            "#aaaaaa"
                                        ]
                                }
                        }
                    ,
                    "splitArea":
                        {
                            "show":
                                false,
                            "areaStyle":
                                {
                                    "color":
                                        [
                                            "#eeeeee"
                                        ]
                                }
                        }
                }
            ,
            "toolbox":
                {
                    "iconStyle":
                        {
                            "borderColor":
                                "#999999"
                        }
                    ,
                    "emphasis":
                        {
                            "iconStyle":
                                {
                                    "borderColor":
                                        "#666666"
                                }
                        }
                }
            ,
            "legend":
                {
                    "textStyle":
                        {
                            "color":
                                "#eeeeee"
                        }
                }
            ,
            "tooltip":
                {
                    "axisPointer":
                        {
                            "lineStyle":
                                {
                                    "color":
                                        "#eeeeee",
                                    "width":
                                        "1"
                                }
                            ,
                            "crossStyle":
                                {
                                    "color":
                                        "#eeeeee",
                                    "width":
                                        "1"
                                }
                        }
                }
            ,
            "timeline":
                {
                    "lineStyle":
                        {
                            "color":
                                "#eeeeee",
                            "width":
                                1
                        }
                    ,
                    "itemStyle":
                        {
                            "color":
                                "#dd6b66",
                            "borderWidth":
                                1
                        }
                    ,
                    "controlStyle":
                        {
                            "color":
                                "#eeeeee",
                            "borderColor":
                                "#eeeeee",
                            "borderWidth":
                                0.5
                        }
                    ,
                    "checkpointStyle":
                        {
                            "color":
                                "#e43c59",
                            "borderColor":
                                "#c23531"
                        }
                    ,
                    "label":
                        {
                            "color":
                                "#eeeeee"
                        }
                    ,
                    "emphasis":
                        {
                            "itemStyle":
                                {
                                    "color":
                                        "#a9334c"
                                }
                            ,
                            "controlStyle":
                                {
                                    "color":
                                        "#eeeeee",
                                    "borderColor":
                                        "#eeeeee",
                                    "borderWidth":
                                        0.5
                                }
                            ,
                            "label":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                }
            ,
            "visualMap":
                {
                    "color":
                        [
                            "#bf444c",
                            "#d88273",
                            "#f6efa6"
                        ]
                }
            ,
            "dataZoom":
                {
                    "backgroundColor":
                        "rgba(47,69,84,0)",
                    "dataBackgroundColor":
                        "rgba(255,255,255,0.3)",
                    "fillerColor":
                        "rgba(167,183,204,0.4)",
                    "handleColor":
                        "#a7b7cc",
                    "handleSize":
                        "100%",
                    "textStyle":
                        {
                            "color":
                                "#eeeeee"
                        }
                }
            ,
            "markPoint":
                {
                    "label":
                        {
                            "color":
                                "#eeeeee"
                        }
                    ,
                    "emphasis":
                        {
                            "label":
                                {
                                    "color":
                                        "#eeeeee"
                                }
                        }
                }
        }
    },
    macarons: {
        name: '马卡龙',
        options: {
            "color": [
                "#2ec7c9",
                "#b6a2de",
                "#5ab1ef",
                "#ffb980",
                "#d87a80",
                "#8d98b3",
                "#e5cf0d",
                "#97b552",
                "#95706d",
                "#dc69aa",
                "#07a2a4",
                "#9a7fd1",
                "#588dd5",
                "#f5994e",
                "#c05050",
                "#59678c",
                "#c9ab00",
                "#7eb00a",
                "#6f5553",
                "#c14089"
            ],
            "backgroundColor": "rgba(0,0,0,0)",
            "textStyle": {},
            "title": {
                "textStyle": {
                    "color": "#008acd"
                },
                "subtextStyle": {
                    "color": "#aaaaaa"
                }
            },
            "line": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 3,
                "symbol": "emptyCircle",
                "smooth": true
            },
            "radar": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 3,
                "symbol": "emptyCircle",
                "smooth": true
            },
            "bar": {
                "itemStyle": {
                    "barBorderWidth": 0,
                    "barBorderColor": "#ccc"
                }
            },
            "pie": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "scatter": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "boxplot": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "parallel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "sankey": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "funnel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "gauge": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "candlestick": {
                "itemStyle": {
                    "color": "#d87a80",
                    "color0": "#2ec7c9",
                    "borderColor": "#d87a80",
                    "borderColor0": "#2ec7c9",
                    "borderWidth": 1
                }
            },
            "graph": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                },
                "lineStyle": {
                    "width": 1,
                    "color": "#aaaaaa"
                },
                "symbolSize": 3,
                "symbol": "emptyCircle",
                "smooth": true,
                "color": [
                    "#2ec7c9",
                    "#b6a2de",
                    "#5ab1ef",
                    "#ffb980",
                    "#d87a80",
                    "#8d98b3",
                    "#e5cf0d",
                    "#97b552",
                    "#95706d",
                    "#dc69aa",
                    "#07a2a4",
                    "#9a7fd1",
                    "#588dd5",
                    "#f5994e",
                    "#c05050",
                    "#59678c",
                    "#c9ab00",
                    "#7eb00a",
                    "#6f5553",
                    "#c14089"
                ],
                "label": {
                    "color": "#eeeeee"
                }
            },
            "map": {
                "itemStyle": {
                    "areaColor": "#dddddd",
                    "borderColor": "#eeeeee",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#d87a80"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(254,153,78,1)",
                        "borderColor": "#444",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(100,0,0)"
                    }
                }
            },
            "geo": {
                "itemStyle": {
                    "areaColor": "#dddddd",
                    "borderColor": "#eeeeee",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#d87a80"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(254,153,78,1)",
                        "borderColor": "#444",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(100,0,0)"
                    }
                }
            },
            "categoryAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#008acd"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#eee"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "valueAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#008acd"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#eee"
                        ]
                    }
                },
                "splitArea": {
                    "show": true,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "logAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#008acd"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#eee"
                        ]
                    }
                },
                "splitArea": {
                    "show": true,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "timeAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#008acd"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#eee"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "toolbox": {
                "iconStyle": {
                    "borderColor": "#2ec7c9"
                },
                "emphasis": {
                    "iconStyle": {
                        "borderColor": "#18a4a6"
                    }
                }
            },
            "legend": {
                "textStyle": {
                    "color": "#333333"
                }
            },
            "tooltip": {
                "axisPointer": {
                    "lineStyle": {
                        "color": "#008acd",
                        "width": "1"
                    },
                    "crossStyle": {
                        "color": "#008acd",
                        "width": "1"
                    }
                }
            },
            "timeline": {
                "lineStyle": {
                    "color": "#008acd",
                    "width": 1
                },
                "itemStyle": {
                    "color": "#008acd",
                    "borderWidth": 1
                },
                "controlStyle": {
                    "color": "#008acd",
                    "borderColor": "#008acd",
                    "borderWidth": 0.5
                },
                "checkpointStyle": {
                    "color": "#2ec7c9",
                    "borderColor": "#2ec7c9"
                },
                "label": {
                    "color": "#008acd"
                },
                "emphasis": {
                    "itemStyle": {
                        "color": "#a9334c"
                    },
                    "controlStyle": {
                        "color": "#008acd",
                        "borderColor": "#008acd",
                        "borderWidth": 0.5
                    },
                    "label": {
                        "color": "#008acd"
                    }
                }
            },
            "visualMap": {
                "color": [
                    "#5ab1ef",
                    "#e0ffff"
                ]
            },
            "dataZoom": {
                "backgroundColor": "rgba(47,69,84,0)",
                "dataBackgroundColor": "#efefff",
                "fillerColor": "rgba(182,162,222,0.2)",
                "handleColor": "#008acd",
                "handleSize": "100%",
                "textStyle": {
                    "color": "#333333"
                }
            },
            "markPoint": {
                "label": {
                    "color": "#eeeeee"
                },
                "emphasis": {
                    "label": {
                        "color": "#eeeeee"
                    }
                }
            }
        }
    },
    shine: {
        name: '闪耀',
        options: {
            "color": [
                "#c12e34",
                "#e6b600",
                "#0098d9",
                "#2b821d",
                "#005eaa",
                "#339ca8",
                "#cda819",
                "#32a487"
            ],
            "backgroundColor": "rgba(0,0,0,0)",
            "textStyle": {},
            "title": {
                "textStyle": {
                    "color": "#333333"
                },
                "subtextStyle": {
                    "color": "#aaaaaa"
                }
            },
            "line": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false
            },
            "radar": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false
            },
            "bar": {
                "itemStyle": {
                    "barBorderWidth": 0,
                    "barBorderColor": "#ccc"
                }
            },
            "pie": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "scatter": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "boxplot": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "parallel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "sankey": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "funnel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "gauge": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "candlestick": {
                "itemStyle": {
                    "color": "#c12e34",
                    "color0": "#2b821d",
                    "borderColor": "#c12e34",
                    "borderColor0": "#2b821d",
                    "borderWidth": 1
                }
            },
            "graph": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                },
                "lineStyle": {
                    "width": 1,
                    "color": "#aaaaaa"
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false,
                "color": [
                    "#c12e34",
                    "#e6b600",
                    "#0098d9",
                    "#2b821d",
                    "#005eaa",
                    "#339ca8",
                    "#cda819",
                    "#32a487"
                ],
                "label": {
                    "color": "#eeeeee"
                }
            },
            "map": {
                "itemStyle": {
                    "areaColor": "#ddd",
                    "borderColor": "#eee",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#c12e34"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "#e6b600",
                        "borderColor": "#ddd",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "#c12e34"
                    }
                }
            },
            "geo": {
                "itemStyle": {
                    "areaColor": "#ddd",
                    "borderColor": "#eee",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#c12e34"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "#e6b600",
                        "borderColor": "#ddd",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "#c12e34"
                    }
                }
            },
            "categoryAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "valueAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "logAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "timeAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "toolbox": {
                "iconStyle": {
                    "borderColor": "#06467c"
                },
                "emphasis": {
                    "iconStyle": {
                        "borderColor": "#4187c2"
                    }
                }
            },
            "legend": {
                "textStyle": {
                    "color": "#333333"
                }
            },
            "tooltip": {
                "axisPointer": {
                    "lineStyle": {
                        "color": "#cccccc",
                        "width": 1
                    },
                    "crossStyle": {
                        "color": "#cccccc",
                        "width": 1
                    }
                }
            },
            "timeline": {
                "lineStyle": {
                    "color": "#005eaa",
                    "width": 1
                },
                "itemStyle": {
                    "color": "#005eaa",
                    "borderWidth": 1
                },
                "controlStyle": {
                    "color": "#005eaa",
                    "borderColor": "#005eaa",
                    "borderWidth": 0.5
                },
                "checkpointStyle": {
                    "color": "#005eaa",
                    "borderColor": "#316bc2"
                },
                "label": {
                    "color": "#005eaa"
                },
                "emphasis": {
                    "itemStyle": {
                        "color": "#005eaa"
                    },
                    "controlStyle": {
                        "color": "#005eaa",
                        "borderColor": "#005eaa",
                        "borderWidth": 0.5
                    },
                    "label": {
                        "color": "#005eaa"
                    }
                }
            },
            "visualMap": {
                "color": [
                    "#1790cf",
                    "#a2d4e6"
                ]
            },
            "dataZoom": {
                "backgroundColor": "rgba(47,69,84,0)",
                "dataBackgroundColor": "rgba(47,69,84,0.3)",
                "fillerColor": "rgba(167,183,204,0.4)",
                "handleColor": "#a7b7cc",
                "handleSize": "100%",
                "textStyle": {
                    "color": "#333333"
                }
            },
            "markPoint": {
                "label": {
                    "color": "#eeeeee"
                },
                "emphasis": {
                    "label": {
                        "color": "#eeeeee"
                    }
                }
            }
        }
    },
    walden: {
        name: '瓦尔登湖',
        options:
            {
                "color": [
                    "#3fb1e3",
                    "#6be6c1",
                    "#626c91",
                    "#a0a7e6",
                    "#c4ebad",
                    "#96dee8"
                ],
                "backgroundColor": "rgba(252,252,252,0)",
                "textStyle": {},
                "title": {
                    "textStyle": {
                        "color": "#666666"
                    },
                    "subtextStyle": {
                        "color": "#999999"
                    }
                },
                "line": {
                    "itemStyle": {
                        "borderWidth": "2"
                    },
                    "lineStyle": {
                        "width": "3"
                    },
                    "symbolSize": "8",
                    "symbol": "emptyCircle",
                    "smooth": false
                },
                "radar": {
                    "itemStyle": {
                        "borderWidth": "2"
                    },
                    "lineStyle": {
                        "width": "3"
                    },
                    "symbolSize": "8",
                    "symbol": "emptyCircle",
                    "smooth": false
                },
                "bar": {
                    "itemStyle": {
                        "barBorderWidth": 0,
                        "barBorderColor": "#ccc"
                    }
                },
                "pie": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    }
                },
                "scatter": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    }
                },
                "boxplot": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    }
                },
                "parallel": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    }
                },
                "sankey": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    }
                },
                "funnel": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    }
                },
                "gauge": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    }
                },
                "candlestick": {
                    "itemStyle": {
                        "color": "#e6a0d2",
                        "color0": "transparent",
                        "borderColor": "#e6a0d2",
                        "borderColor0": "#3fb1e3",
                        "borderWidth": "2"
                    }
                },
                "graph": {
                    "itemStyle": {
                        "borderWidth": 0,
                        "borderColor": "#ccc"
                    },
                    "lineStyle": {
                        "width": "1",
                        "color": "#cccccc"
                    },
                    "symbolSize": "8",
                    "symbol": "emptyCircle",
                    "smooth": false,
                    "color": [
                        "#3fb1e3",
                        "#6be6c1",
                        "#626c91",
                        "#a0a7e6",
                        "#c4ebad",
                        "#96dee8"
                    ],
                    "label": {
                        "color": "#ffffff"
                    }
                },
                "map": {
                    "itemStyle": {
                        "areaColor": "#eeeeee",
                        "borderColor": "#aaaaaa",
                        "borderWidth": 0.5
                    },
                    "label": {
                        "color": "#ffffff"
                    },
                    "emphasis": {
                        "itemStyle": {
                            "areaColor": "rgba(63,177,227,0.25)",
                            "borderColor": "#3fb1e3",
                            "borderWidth": 1
                        },
                        "label": {
                            "color": "#3fb1e3"
                        }
                    }
                },
                "geo": {
                    "itemStyle": {
                        "areaColor": "#eeeeee",
                        "borderColor": "#aaaaaa",
                        "borderWidth": 0.5
                    },
                    "label": {
                        "color": "#ffffff"
                    },
                    "emphasis": {
                        "itemStyle": {
                            "areaColor": "rgba(63,177,227,0.25)",
                            "borderColor": "#3fb1e3",
                            "borderWidth": 1
                        },
                        "label": {
                            "color": "#3fb1e3"
                        }
                    }
                },
                "categoryAxis": {
                    "axisLine": {
                        "show": true,
                        "lineStyle": {
                            "color": "#cccccc"
                        }
                    },
                    "axisTick": {
                        "show": false,
                        "lineStyle": {
                            "color": "#333"
                        }
                    },
                    "axisLabel": {
                        "show": true,
                        "color": "#999999"
                    },
                    "splitLine": {
                        "show": true,
                        "lineStyle": {
                            "color": [
                                "#eeeeee"
                            ]
                        }
                    },
                    "splitArea": {
                        "show": false,
                        "areaStyle": {
                            "color": [
                                "rgba(250,250,250,0.05)",
                                "rgba(200,200,200,0.02)"
                            ]
                        }
                    }
                },
                "valueAxis": {
                    "axisLine": {
                        "show": true,
                        "lineStyle": {
                            "color": "#cccccc"
                        }
                    },
                    "axisTick": {
                        "show": false,
                        "lineStyle": {
                            "color": "#333"
                        }
                    },
                    "axisLabel": {
                        "show": true,
                        "color": "#999999"
                    },
                    "splitLine": {
                        "show": true,
                        "lineStyle": {
                            "color": [
                                "#eeeeee"
                            ]
                        }
                    },
                    "splitArea": {
                        "show": false,
                        "areaStyle": {
                            "color": [
                                "rgba(250,250,250,0.05)",
                                "rgba(200,200,200,0.02)"
                            ]
                        }
                    }
                },
                "logAxis": {
                    "axisLine": {
                        "show": true,
                        "lineStyle": {
                            "color": "#cccccc"
                        }
                    },
                    "axisTick": {
                        "show": false,
                        "lineStyle": {
                            "color": "#333"
                        }
                    },
                    "axisLabel": {
                        "show": true,
                        "color": "#999999"
                    },
                    "splitLine": {
                        "show": true,
                        "lineStyle": {
                            "color": [
                                "#eeeeee"
                            ]
                        }
                    },
                    "splitArea": {
                        "show": false,
                        "areaStyle": {
                            "color": [
                                "rgba(250,250,250,0.05)",
                                "rgba(200,200,200,0.02)"
                            ]
                        }
                    }
                },
                "timeAxis": {
                    "axisLine": {
                        "show": true,
                        "lineStyle": {
                            "color": "#cccccc"
                        }
                    },
                    "axisTick": {
                        "show": false,
                        "lineStyle": {
                            "color": "#333"
                        }
                    },
                    "axisLabel": {
                        "show": true,
                        "color": "#999999"
                    },
                    "splitLine": {
                        "show": true,
                        "lineStyle": {
                            "color": [
                                "#eeeeee"
                            ]
                        }
                    },
                    "splitArea": {
                        "show": false,
                        "areaStyle": {
                            "color": [
                                "rgba(250,250,250,0.05)",
                                "rgba(200,200,200,0.02)"
                            ]
                        }
                    }
                },
                "toolbox": {
                    "iconStyle": {
                        "borderColor": "#999999"
                    },
                    "emphasis": {
                        "iconStyle": {
                            "borderColor": "#666666"
                        }
                    }
                },
                "legend": {
                    "textStyle": {
                        "color": "#999999"
                    }
                },
                "tooltip": {
                    "axisPointer": {
                        "lineStyle": {
                            "color": "#cccccc",
                            "width": 1
                        },
                        "crossStyle": {
                            "color": "#cccccc",
                            "width": 1
                        }
                    }
                },
                "timeline": {
                    "lineStyle": {
                        "color": "#626c91",
                        "width": 1
                    },
                    "itemStyle": {
                        "color": "#626c91",
                        "borderWidth": 1
                    },
                    "controlStyle": {
                        "color": "#626c91",
                        "borderColor": "#626c91",
                        "borderWidth": 0.5
                    },
                    "checkpointStyle": {
                        "color": "#3fb1e3",
                        "borderColor": "#3fb1e3"
                    },
                    "label": {
                        "color": "#626c91"
                    },
                    "emphasis": {
                        "itemStyle": {
                            "color": "#626c91"
                        },
                        "controlStyle": {
                            "color": "#626c91",
                            "borderColor": "#626c91",
                            "borderWidth": 0.5
                        },
                        "label": {
                            "color": "#626c91"
                        }
                    }
                },
                "visualMap": {
                    "color": [
                        "#2a99c9",
                        "#afe8ff"
                    ]
                },
                "dataZoom": {
                    "backgroundColor": "rgba(255,255,255,0)",
                    "dataBackgroundColor": "rgba(222,222,222,1)",
                    "fillerColor": "rgba(114,230,212,0.25)",
                    "handleColor": "#cccccc",
                    "handleSize": "100%",
                    "textStyle": {
                        "color": "#999999"
                    }
                },
                "markPoint": {
                    "label": {
                        "color": "#ffffff"
                    },
                    "emphasis": {
                        "label": {
                            "color": "#ffffff"
                        }
                    }
                }
            }

    },
    wonderland: {
        name: '仙境',
        options: {
            "color": [
                "#4ea397",
                "#22c3aa",
                "#7bd9a5",
                "#d0648a",
                "#f58db2",
                "#f2b3c9"
            ],
            "backgroundColor": "rgba(255,255,255,0)",
            "textStyle": {},
            "title": {
                "textStyle": {
                    "color": "#666666"
                },
                "subtextStyle": {
                    "color": "#999999"
                }
            },
            "line": {
                "itemStyle": {
                    "borderWidth": "2"
                },
                "lineStyle": {
                    "width": "3"
                },
                "symbolSize": "8",
                "symbol": "emptyCircle",
                "smooth": false
            },
            "radar": {
                "itemStyle": {
                    "borderWidth": "2"
                },
                "lineStyle": {
                    "width": "3"
                },
                "symbolSize": "8",
                "symbol": "emptyCircle",
                "smooth": false
            },
            "bar": {
                "itemStyle": {
                    "barBorderWidth": 0,
                    "barBorderColor": "#ccc"
                }
            },
            "pie": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "scatter": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "boxplot": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "parallel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "sankey": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "funnel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "gauge": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "candlestick": {
                "itemStyle": {
                    "color": "#d0648a",
                    "color0": "transparent",
                    "borderColor": "#d0648a",
                    "borderColor0": "#22c3aa",
                    "borderWidth": "1"
                }
            },
            "graph": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                },
                "lineStyle": {
                    "width": "1",
                    "color": "#cccccc"
                },
                "symbolSize": "8",
                "symbol": "emptyCircle",
                "smooth": false,
                "color": [
                    "#4ea397",
                    "#22c3aa",
                    "#7bd9a5",
                    "#d0648a",
                    "#f58db2",
                    "#f2b3c9"
                ],
                "label": {
                    "color": "#ffffff"
                }
            },
            "map": {
                "itemStyle": {
                    "areaColor": "#eeeeee",
                    "borderColor": "#999999",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#28544e"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(34,195,170,0.25)",
                        "borderColor": "#22c3aa",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "#349e8e"
                    }
                }
            },
            "geo": {
                "itemStyle": {
                    "areaColor": "#eeeeee",
                    "borderColor": "#999999",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#28544e"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(34,195,170,0.25)",
                        "borderColor": "#22c3aa",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "#349e8e"
                    }
                }
            },
            "categoryAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#cccccc"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#999999"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#eeeeee"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "valueAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#cccccc"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#999999"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#eeeeee"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "logAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#cccccc"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#999999"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#eeeeee"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "timeAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#cccccc"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#999999"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#eeeeee"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "toolbox": {
                "iconStyle": {
                    "borderColor": "#999999"
                },
                "emphasis": {
                    "iconStyle": {
                        "borderColor": "#666666"
                    }
                }
            },
            "legend": {
                "textStyle": {
                    "color": "#999999"
                }
            },
            "tooltip": {
                "axisPointer": {
                    "lineStyle": {
                        "color": "#cccccc",
                        "width": 1
                    },
                    "crossStyle": {
                        "color": "#cccccc",
                        "width": 1
                    }
                }
            },
            "timeline": {
                "lineStyle": {
                    "color": "#4ea397",
                    "width": 1
                },
                "itemStyle": {
                    "color": "#4ea397",
                    "borderWidth": 1
                },
                "controlStyle": {
                    "color": "#4ea397",
                    "borderColor": "#4ea397",
                    "borderWidth": 0.5
                },
                "checkpointStyle": {
                    "color": "#4ea397",
                    "borderColor": "#3cebd2"
                },
                "label": {
                    "color": "#4ea397"
                },
                "emphasis": {
                    "itemStyle": {
                        "color": "#4ea397"
                    },
                    "controlStyle": {
                        "color": "#4ea397",
                        "borderColor": "#4ea397",
                        "borderWidth": 0.5
                    },
                    "label": {
                        "color": "#4ea397"
                    }
                }
            },
            "visualMap": {
                "color": [
                    "#d0648a",
                    "#22c3aa",
                    "#adfff1"
                ]
            },
            "dataZoom": {
                "backgroundColor": "rgba(255,255,255,0)",
                "dataBackgroundColor": "rgba(222,222,222,1)",
                "fillerColor": "rgba(114,230,212,0.25)",
                "handleColor": "#cccccc",
                "handleSize": "100%",
                "textStyle": {
                    "color": "#999999"
                }
            },
            "markPoint": {
                "label": {
                    "color": "#ffffff"
                },
                "emphasis": {
                    "label": {
                        "color": "#ffffff"
                    }
                }
            }
        }
    },
    chalk: {
        name: '粉色',
        options: {
            "color": [
                "#fc97af",
                "#87f7cf",
                "#f7f494",
                "#72ccff",
                "#f7c5a0",
                "#d4a4eb",
                "#d2f5a6",
                "#76f2f2"
            ],
            "backgroundColor": "rgba(41,52,65,1)",
            "textStyle": {},
            "title": {
                "textStyle": {
                    "color": "#ffffff"
                },
                "subtextStyle": {
                    "color": "#dddddd"
                }
            },
            "line": {
                "itemStyle": {
                    "borderWidth": "4"
                },
                "lineStyle": {
                    "width": "3"
                },
                "symbolSize": "0",
                "symbol": "circle",
                "smooth": true
            },
            "radar": {
                "itemStyle": {
                    "borderWidth": "4"
                },
                "lineStyle": {
                    "width": "3"
                },
                "symbolSize": "0",
                "symbol": "circle",
                "smooth": true
            },
            "bar": {
                "itemStyle": {
                    "barBorderWidth": 0,
                    "barBorderColor": "#ccc"
                }
            },
            "pie": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "scatter": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "boxplot": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "parallel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "sankey": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "funnel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "gauge": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "candlestick": {
                "itemStyle": {
                    "color": "#fc97af",
                    "color0": "transparent",
                    "borderColor": "#fc97af",
                    "borderColor0": "#87f7cf",
                    "borderWidth": "2"
                }
            },
            "graph": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                },
                "lineStyle": {
                    "width": "1",
                    "color": "#ffffff"
                },
                "symbolSize": "0",
                "symbol": "circle",
                "smooth": true,
                "color": [
                    "#fc97af",
                    "#87f7cf",
                    "#f7f494",
                    "#72ccff",
                    "#f7c5a0",
                    "#d4a4eb",
                    "#d2f5a6",
                    "#76f2f2"
                ],
                "label": {
                    "color": "#293441"
                }
            },
            "map": {
                "itemStyle": {
                    "areaColor": "#f3f3f3",
                    "borderColor": "#999999",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#893448"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(255,178,72,1)",
                        "borderColor": "#eb8146",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(137,52,72)"
                    }
                }
            },
            "geo": {
                "itemStyle": {
                    "areaColor": "#f3f3f3",
                    "borderColor": "#999999",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#893448"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(255,178,72,1)",
                        "borderColor": "#eb8146",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(137,52,72)"
                    }
                }
            },
            "categoryAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#666666"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#aaaaaa"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#e6e6e6"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "valueAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#666666"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#aaaaaa"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#e6e6e6"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "logAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#666666"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#aaaaaa"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#e6e6e6"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "timeAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#666666"
                    }
                },
                "axisTick": {
                    "show": false,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#aaaaaa"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#e6e6e6"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.05)",
                            "rgba(200,200,200,0.02)"
                        ]
                    }
                }
            },
            "toolbox": {
                "iconStyle": {
                    "borderColor": "#999999"
                },
                "emphasis": {
                    "iconStyle": {
                        "borderColor": "#666666"
                    }
                }
            },
            "legend": {
                "textStyle": {
                    "color": "#999999"
                }
            },
            "tooltip": {
                "axisPointer": {
                    "lineStyle": {
                        "color": "#cccccc",
                        "width": 1
                    },
                    "crossStyle": {
                        "color": "#cccccc",
                        "width": 1
                    }
                }
            },
            "timeline": {
                "lineStyle": {
                    "color": "#87f7cf",
                    "width": 1
                },
                "itemStyle": {
                    "color": "#87f7cf",
                    "borderWidth": 1
                },
                "controlStyle": {
                    "color": "#87f7cf",
                    "borderColor": "#87f7cf",
                    "borderWidth": 0.5
                },
                "checkpointStyle": {
                    "color": "#fc97af",
                    "borderColor": "#fc97af"
                },
                "label": {
                    "color": "#87f7cf"
                },
                "emphasis": {
                    "itemStyle": {
                        "color": "#f7f494"
                    },
                    "controlStyle": {
                        "color": "#87f7cf",
                        "borderColor": "#87f7cf",
                        "borderWidth": 0.5
                    },
                    "label": {
                        "color": "#87f7cf"
                    }
                }
            },
            "visualMap": {
                "color": [
                    "#fc97af",
                    "#87f7cf"
                ]
            },
            "dataZoom": {
                "backgroundColor": "rgba(255,255,255,0)",
                "dataBackgroundColor": "rgba(114,204,255,1)",
                "fillerColor": "rgba(114,204,255,0.2)",
                "handleColor": "#72ccff",
                "handleSize": "100%",
                "textStyle": {
                    "color": "#333333"
                }
            },
            "markPoint": {
                "label": {
                    "color": "#293441"
                },
                "emphasis": {
                    "label": {
                        "color": "#293441"
                    }
                }
            }
        }
    },
    roma: {
        name: '罗马',
        options: {
            "color": [
                "#e01f54",
                "#001852",
                "#f5e8c8",
                "#b8d2c7",
                "#c6b38e",
                "#a4d8c2",
                "#f3d999",
                "#d3758f",
                "#dcc392",
                "#2e4783",
                "#82b6e9",
                "#ff6347",
                "#a092f1",
                "#0a915d",
                "#eaf889",
                "#6699FF",
                "#ff6666",
                "#3cb371",
                "#d5b158",
                "#38b6b6"
            ],
            "backgroundColor": "rgba(0,0,0,0)",
            "textStyle": {},
            "title": {
                "textStyle": {
                    "color": "#333333"
                },
                "subtextStyle": {
                    "color": "#aaaaaa"
                }
            },
            "line": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false
            },
            "radar": {
                "itemStyle": {
                    "borderWidth": 1
                },
                "lineStyle": {
                    "width": 2
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false
            },
            "bar": {
                "itemStyle": {
                    "barBorderWidth": 0,
                    "barBorderColor": "#ccc"
                }
            },
            "pie": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "scatter": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "boxplot": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "parallel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "sankey": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "funnel": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "gauge": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                }
            },
            "candlestick": {
                "itemStyle": {
                    "color": "#e01f54",
                    "color0": "#001852",
                    "borderColor": "#f5e8c8",
                    "borderColor0": "#b8d2c7",
                    "borderWidth": 1
                }
            },
            "graph": {
                "itemStyle": {
                    "borderWidth": 0,
                    "borderColor": "#ccc"
                },
                "lineStyle": {
                    "width": 1,
                    "color": "#aaaaaa"
                },
                "symbolSize": 4,
                "symbol": "emptyCircle",
                "smooth": false,
                "color": [
                    "#e01f54",
                    "#001852",
                    "#f5e8c8",
                    "#b8d2c7",
                    "#c6b38e",
                    "#a4d8c2",
                    "#f3d999",
                    "#d3758f",
                    "#dcc392",
                    "#2e4783",
                    "#82b6e9",
                    "#ff6347",
                    "#a092f1",
                    "#0a915d",
                    "#eaf889",
                    "#6699FF",
                    "#ff6666",
                    "#3cb371",
                    "#d5b158",
                    "#38b6b6"
                ],
                "label": {
                    "color": "#eeeeee"
                }
            },
            "map": {
                "itemStyle": {
                    "areaColor": "#eeeeee",
                    "borderColor": "#444444",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#000000"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(255,215,0,0.8)",
                        "borderColor": "#444",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(100,0,0)"
                    }
                }
            },
            "geo": {
                "itemStyle": {
                    "areaColor": "#eeeeee",
                    "borderColor": "#444444",
                    "borderWidth": 0.5
                },
                "label": {
                    "color": "#000000"
                },
                "emphasis": {
                    "itemStyle": {
                        "areaColor": "rgba(255,215,0,0.8)",
                        "borderColor": "#444",
                        "borderWidth": 1
                    },
                    "label": {
                        "color": "rgb(100,0,0)"
                    }
                }
            },
            "categoryAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": false,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "valueAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "logAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "timeAxis": {
                "axisLine": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisTick": {
                    "show": true,
                    "lineStyle": {
                        "color": "#333"
                    }
                },
                "axisLabel": {
                    "show": true,
                    "color": "#333"
                },
                "splitLine": {
                    "show": true,
                    "lineStyle": {
                        "color": [
                            "#ccc"
                        ]
                    }
                },
                "splitArea": {
                    "show": false,
                    "areaStyle": {
                        "color": [
                            "rgba(250,250,250,0.3)",
                            "rgba(200,200,200,0.3)"
                        ]
                    }
                }
            },
            "toolbox": {
                "iconStyle": {
                    "borderColor": "#999999"
                },
                "emphasis": {
                    "iconStyle": {
                        "borderColor": "#666666"
                    }
                }
            },
            "legend": {
                "textStyle": {
                    "color": "#333333"
                }
            },
            "tooltip": {
                "axisPointer": {
                    "lineStyle": {
                        "color": "#cccccc",
                        "width": 1
                    },
                    "crossStyle": {
                        "color": "#cccccc",
                        "width": 1
                    }
                }
            },
            "timeline": {
                "lineStyle": {
                    "color": "#293c55",
                    "width": 1
                },
                "itemStyle": {
                    "color": "#293c55",
                    "borderWidth": 1
                },
                "controlStyle": {
                    "color": "#293c55",
                    "borderColor": "#293c55",
                    "borderWidth": 0.5
                },
                "checkpointStyle": {
                    "color": "#e43c59",
                    "borderColor": "#c23531"
                },
                "label": {
                    "color": "#293c55"
                },
                "emphasis": {
                    "itemStyle": {
                        "color": "#a9334c"
                    },
                    "controlStyle": {
                        "color": "#293c55",
                        "borderColor": "#293c55",
                        "borderWidth": 0.5
                    },
                    "label": {
                        "color": "#293c55"
                    }
                }
            },
            "visualMap": {
                "color": [
                    "#e01f54",
                    "#e7dbc3"
                ]
            },
            "dataZoom": {
                "backgroundColor": "rgba(47,69,84,0)",
                "dataBackgroundColor": "rgba(47,69,84,0.3)",
                "fillerColor": "rgba(167,183,204,0.4)",
                "handleColor": "#a7b7cc",
                "handleSize": "100%",
                "textStyle": {
                    "color": "#333333"
                }
            },
            "markPoint": {
                "label": {
                    "color": "#eeeeee"
                },
                "emphasis": {
                    "label": {
                        "color": "#eeeeee"
                    }
                }
            }
        }
    },
}

const keys = Object.keys(echartsThemes)

keys.forEach(t => {
    echarts.registerTheme(t, echartsThemes[t].options)
})




