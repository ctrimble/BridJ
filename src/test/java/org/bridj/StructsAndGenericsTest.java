package org.bridj;

import static org.junit.Assert.*;

import org.bridj.ann.Field;
import org.junit.Ignore;
import org.junit.Test;

public class StructsAndGenericsTest {
	
	/**
	 * A struct value to nest in other structs.
	 */
	public static class GenericValue extends StructObject {
		public static final int VALUE_INDEX = 0;

		@Field(VALUE_INDEX)
		public int value() {
			return this.io.getIntField(this, VALUE_INDEX);
	  }

		@Field(VALUE_INDEX)
		public GenericValue value(int value) {
			this.io.setIntField(this, VALUE_INDEX, value);
			return this;
	  }		
	}
	
	/**
	 * A parent struct where the child is defined as abstract methods.
	 *
	 * @param <C> The child type
	 * @param <S> The parent type
	 */
	public static abstract class AbstractParentStruct<C extends StructObject, S extends AbstractParentStruct<C, S>> extends StructObject {
		
		public static final int CHILD_INDEX = 0;

		@SuppressWarnings("unchecked")
    protected S thisObject() {
			return (S)this;
		}

		public abstract C child();

		public abstract S child(C child);
	}
	
	/**
	 * A type that ties the abstract parent struct with a specific child type.
	 */
	public static class AbstractParentWithValue extends AbstractParentStruct<GenericValue, AbstractParentWithValue> {

		@Override
		@Field(CHILD_INDEX)
    public GenericValue child() {
			return this.io.getNativeObjectField(this, CHILD_INDEX);
    }

		@Override
		@Field(CHILD_INDEX)
    public AbstractParentWithValue child(GenericValue child) {
			this.io.setNativeObjectField(this, CHILD_INDEX, child);
			return thisObject();
    }
	}
		
	@Test
	public void shouldCreateAbstractParent() {
		Pointer<AbstractParentWithValue> pointer = Pointer.allocate(AbstractParentWithValue.class);
	}
	
	/**
	 * A parent struct where the child is defined as abstract methods with field definitions.
	 *
	 * @param <C> The child type
	 * @param <S> The parent type
	 */
	public static abstract class InheritedFieldStruct<C extends StructObject, S extends InheritedFieldStruct<C, S>> extends StructObject {
		
		public static final int CHILD_INDEX = 0;

		@SuppressWarnings("unchecked")
    protected S thisObject() {
			return (S)this;
		}

		@Field(CHILD_INDEX)
		public abstract C child();

		@Field(CHILD_INDEX)
		public abstract S child(C child);
	}
	
	public static class InheritedFieldStructWithValue extends InheritedFieldStruct<GenericValue, InheritedFieldStructWithValue> {

		@Override
    public GenericValue child() {
			return this.io.getNativeObjectField(this, CHILD_INDEX);
    }

		@Override
    public InheritedFieldStructWithValue child(GenericValue child) {
			this.io.setNativeObjectField(this, CHILD_INDEX, child);
			return thisObject();
    }
	}
	
  @Test
  public void shouldCreateInheritedFieldStruct() {
	  Pointer<InheritedFieldStructWithValue> pointer = Pointer.allocate(InheritedFieldStructWithValue.class);
  }

	
	public static class ParentStruct<C extends StructObject, S extends ParentStruct<C, S>> extends StructObject {
		
		public static final int CHILD_INDEX = 0;

		@SuppressWarnings("unchecked")
    protected S thisObject() {
			return (S)this;
		}

		@Field(CHILD_INDEX)
		public C child() {
			return this.io.getNativeObjectField(this, CHILD_INDEX);
	  }

		@Field(CHILD_INDEX)
		public S child(C child) {
			this.io.setNativeObjectField(this, CHILD_INDEX, child);
			return thisObject();
	  }
	}

	
	public class ParentStructWithValue extends ParentStruct<GenericValue, ParentStructWithValue> {}
	
	@Test
	public void shouldCreateStructWithGenericChild() {
		Pointer<ParentStructWithValue> pointer = Pointer.allocate(ParentStructWithValue.class);
	}

}
