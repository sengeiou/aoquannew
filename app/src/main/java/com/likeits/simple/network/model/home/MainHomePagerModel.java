package com.likeits.simple.network.model.home;

import java.io.Serializable;

public class MainHomePagerModel extends HomeMessage{

    /**
     * type : 2
     * title : 大商云－首页
     * name : 商城首页
     * desc :
     * icon :
     * background : #f3f3f3
     * titlebarbg : #ffffff
     * titlebarcolor : #000000
     */

    private String type;
    private String title;
    private String name;
    private String desc;
    private String icon;
    private String background;
    private String titlebarbg;
    private String titlebarcolor;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getTitlebarbg() {
        return titlebarbg;
    }

    public void setTitlebarbg(String titlebarbg) {
        this.titlebarbg = titlebarbg;
    }

    public String getTitlebarcolor() {
        return titlebarcolor;
    }

    public void setTitlebarcolor(String titlebarcolor) {
        this.titlebarcolor = titlebarcolor;
    }
}
