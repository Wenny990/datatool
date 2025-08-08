import RightContextMenu from './index.vue'
import {createApp} from "vue";

let app;
let div;
const onRemove = () => {
    app && app.unmount();
    div && div.remove();
}

/**
 * 打开右键菜单
 * @param position 位置信息
 * @param menuList 菜单项
 * @param clickHandler 点击事件
 */
export const showRightMenu = (position, menuList, clickHandler) => {
    onRemove();
     div = document.createElement('div');
    document.body.appendChild(div)
     app = createApp(RightContextMenu, {
         position , menuList, onClick: (name) => {
             onRemove();
            clickHandler & clickHandler(name)
        },
        onRemove
    });
    app.mount(div);
}

