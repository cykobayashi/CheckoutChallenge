package com.acme.checkout.domain.model;

import java.util.*;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document(collection = "users")
@Data
@Builder
public class User implements UserDetails {

	private static final long serialVersionUID = 4815877135015943617L;

	@Id
	private String id;

	private String username;

	private String email;

	private String guid;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Privilege> privileges = new HashSet<>();

		return privileges;
	}

	@Override
    public String getPassword() {
        return null;
    }

    @Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) &&
				Objects.equals(email, user.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, email);
	}

}
