package com.capturenow.module;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Albums {
	
	@Id
	private String id;
	
	private String name;
	
	private String type;
	
	@Lob
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "photo")
	private byte[] photo;
	
	private String category;
	
	@JsonBackReference
	@ManyToOne
	private Photographer photographer;

	@PrePersist
	private void generateCustomId() {
		if (this.id == null) {
			this.id = "CN-A" + UUID.randomUUID().toString();
		}
	}
}
