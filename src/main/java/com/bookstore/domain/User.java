package com.bookstore.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.bookstore.domain.security.Authority;
import com.bookstore.domain.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {


   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String userName;



   private String password;
   private String firstName;
   private String lastName;

   @Column(name = "email")
   private String email;
   private String phone;

   private boolean enabled = true;

   public Set<UserRole> getUserRoles() {
      return this.userRoles;
   }

   public void setUserRoles(Set<UserRole> userRoles) {
      this.userRoles = userRoles;
   }

   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @JsonIgnore
   private Set<UserRole> userRoles = new HashSet<>();

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      Set<GrantedAuthority> authorities = new HashSet<>();
      userRoles.forEach(ur -> authorities.add(new Authority(ur.getRole().getName())));
      return authorities;
   }

   @Override
   public boolean isAccountNonExpired() {
      // TODO Auto-generated method stub
      return true;
   }

   @Override
   public boolean isAccountNonLocked() {
      // TODO Auto-generated method stub
      return true;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      // TODO Auto-generated method stub
      return true;
   }
   
   // @Override
   // private boolean isEnabled(){
   //    return enabled;
   // }
   @Override
   public boolean isEnabled() {
      return enabled;
   }

   @Override
   public String getUsername() {
      // TODO Auto-generated method stub
      return userName;
   }
}