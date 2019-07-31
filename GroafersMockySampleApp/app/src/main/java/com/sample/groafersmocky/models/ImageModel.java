package com.sample.groafersmocky.models;

import org.json.JSONObject;

public class ImageModel {

    String id;
    String owner;
    String secret;
    String server;
    int farm;
    String title;
    boolean ispublic;
    boolean isfriend;
    boolean isfamily;


    public ImageModel(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("id");
            this.owner = jsonObject.getString("owner");
            this.secret = jsonObject.getString("secret");
            this.server = jsonObject.getString("server");
            this.farm = jsonObject.getInt("farm");
            this.title = jsonObject.getString("title");
            this.ispublic = jsonObject.getInt("ispublic") == 1 ? true : false;
            this.isfriend = jsonObject.getInt("isfriend") == 1 ? true : false;
            this.isfamily = jsonObject.getInt("isfamily") == 1 ? true : false;
        } catch (Exception e) {
        }
    }

    public ImageModel(String id, String owner, String secret, String server, int farm, String title, boolean ispublic, boolean isfriend, boolean isfamily) {
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
        this.ispublic = ispublic;
        this.isfriend = isfriend;
        this.isfamily = isfamily;
    }

    public String getImageUrl() {
        return "https://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
}
