package io.github.gabrielmmoraes1999.service.entity;

import io.github.gabrielmmoraes1999.db.annotation.Column;
import io.github.gabrielmmoraes1999.db.annotation.PrimaryKey;
import io.github.gabrielmmoraes1999.db.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "CLIENT")
public class Client {

    @PrimaryKey
    @Column(name = "ID_CLIENT")
    private Integer idClient;

    @Column(name = "NAME")
    private String name;

    @Column(name = "email")
    private String email;

    public Client() {

    }

}
