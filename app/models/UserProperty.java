package models;

import play.data.validation.Constraints;

/**
 * ユーザ情報のバリデーション用
 */
public class UserProperty {
    public String userName;

    public String biography;

    public UserProperty() {
        super();
    }

    public UserProperty(String userName, String biography) {
        this.userName = userName;
        this.biography = biography;
    }
}
