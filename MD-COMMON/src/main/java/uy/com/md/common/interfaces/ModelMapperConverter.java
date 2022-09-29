package uy.com.md.common.interfaces;

public interface ModelMapperConverter {

    public Object convertToDto(Object object);

    public Object convertToEntity(Object object);
}
