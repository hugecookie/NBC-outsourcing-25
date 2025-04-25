package org.example.outsourcing.domain.user.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.outsourcing.common.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(
	name = "user",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"email", "platform"})
	}
)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column
	private String name;

	@Column
	private String password;

	@ElementCollection
	@Builder.Default
	private List<String> roles = new ArrayList<>();

	@Column
	private boolean isDeleted;

	@Column
	private String profileImgUrl;

	@Enumerated(EnumType.STRING)
	private Platform platform;

	public void withdraw() {
		this.name = "deleted user" + UUID.randomUUID();
		this.isDeleted = true;
		this.roles.clear();
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}

	public void changeProfileImage(String imageUrl) {
		this.profileImgUrl = imageUrl;
	}

	public void applyDefaultProfileImage() {
		this.profileImgUrl = "https://hugecookie-out-sourcing.s3.ap-northeast-2.amazonaws.com/default-image.png";
	}
}
