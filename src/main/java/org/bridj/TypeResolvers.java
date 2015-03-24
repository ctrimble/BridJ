package org.bridj;

import java.lang.reflect.Type;

import com.fasterxml.classmate.Filter;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;

public class TypeResolvers {
  private static final TypeResolver RESOLVER = new TypeResolver();

  public static ResolvedType resolveType(Type type) {
  	if( type == null ) return null;
  	return RESOLVER.resolve(type);
  }
  
  static ResolvedType resolveType( StructFieldDescription decl, Type structType ) {
  	ResolvedType resolvedInstance = RESOLVER.resolve(structType);
  	MemberResolver memberResolver = memberResolver(decl);
  	ResolvedTypeWithMembers declType = memberResolver.resolve(resolvedInstance, null, null);
  	if( decl.getter != null ) {
  	  ResolvedMethod[] memberMethods = declType.getMemberMethods();
  	  return memberMethods[0].getReturnType();
  	}
  	else if( decl.field != null ) {
  		ResolvedField[] memberFields = declType.getMemberFields();
  		return memberFields[0].getType();
  	}
  	else {
  		throw new IllegalStateException("No getter of field on description.");
  	}
  }
 
 private static MemberResolver memberResolver( final StructFieldDescription desc ) {
 	return new MemberResolver(RESOLVER)
 	  .setMethodFilter(new Filter<RawMethod>() {
       public boolean include(RawMethod raw) {
         return desc.getter != null && raw.getRawMember().equals(desc.getter);
       }
 	  })
 	  .setFieldFilter(new Filter<RawField>() {
			@Override
      public boolean include(RawField raw) {
	      return desc.field != null && raw.getRawMember().equals(desc.field);
      }
 	  });
 }
}
