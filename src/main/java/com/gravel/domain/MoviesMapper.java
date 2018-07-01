package com.gravel.domain;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
/**
 * Created by gravel on 2018/04/13.
 */
@Mapper
public interface MoviesMapper {

    @Insert("INSERT INTO movies(`av_code`,`publish_date`, `footage`, `director`, `publisher`, `manufacturer`, `series`, `categories`, `tag`, `download_url`, `pic_url`) VALUES ( #{avCode},#{publishDate},#{footage},#{director},#{publisher},#{manufacturer},#{series},#{categories},#{tag},#{downloadUrl},#{picUrl})")
    void insert(Movies movies);
}