package com.qthegamep.bookmanager2.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * This class is an entity.
 * There is an no args constructor, getters and setters for fields, override equals, hashcode and toString methods.
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "BOOKS")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Column(name = "PRINT_YEAR", nullable = false)
    private int printYear;

    @Column(name = "IS_READ", nullable = false)
    private boolean isRead;
}
