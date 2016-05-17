package models;

import play.data.validation.Constraints;

/**
 * ユーザ情報のバリデーション用
 */
public class UserProperty {
    @Constraints.Pattern(message = "ユーザ名は英数字で0-140文字です",
            value = "\\w{0,140}")
    public String userName;

    @Constraints.Pattern(message = "プロフィールは英数字で0-140文字です．",
            value = "\\w{0,140}")
    public String biography;
}
