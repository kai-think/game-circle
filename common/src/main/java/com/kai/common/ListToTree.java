package com.kai.common;

import com.example.demo.sys.entity.Menu;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListToTree {

    public static void listToTree(@NonNull List<? extends EnableListToTree> list) {

        Map<?, EnableListToTree> itemMap = list.stream()  //以id为键，以item为值
                .collect(Collectors.toMap(EnableListToTree::id, o -> o));

        //进行变换，结果形成item树
        for (int i = 0; i < list.size(); i++) {    //遍历itemList
            EnableListToTree item = list.get(i);
            Object parentId = item.parentId();
            //如果item是根菜单，不做操作
            if (parentId == null)
                continue;

            //如果item不是根菜单，将其加到 父菜单的 子孙里面， 并从itemList中移除
            EnableListToTree parentItem = itemMap.get(parentId);
            if (parentItem == null)
                continue;
            List<EnableListToTree> childs = parentItem.children();
            if (childs == null)
            {
                childs = new ArrayList<>();
                parentItem.setChildren(childs);
            }

            childs.add(item);
            list.remove(item);
            i--;
        }
    }
}
