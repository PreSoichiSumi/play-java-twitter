package models;

import com.avaje.ebean.Model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by s-sumi on 2016/05/08.
 */
public class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long relationId;

    public String userId;


    public static Model.Finder<Long, Relation> find = new Model.Finder<Long, Relation>(Relation.class);
}
