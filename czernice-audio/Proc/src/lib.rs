use mvutils::utils::TetrahedronOp;
use proc_macro::TokenStream;
use quote::ToTokens;
use std::fs::OpenOptions;
use std::io::Write;
use syn::meta::parser;
use syn::{parse_macro_input, Data, DeriveInput, Fields, LitStr};

const ALLOCATION_TYPES: [(&str, &str, &str); 5] = [
    ("malloc", "", "MemoryUtil.malloc(SIZEOF), null"),
    ("calloc", "", "MemoryUtil.calloc(SIZEOF), null"),
    ("create", "long address", "address, null"),
    ("malloc", "MemoryStack stack", "stack.malloc(SIZEOF), null"),
    ("calloc", "MemoryStack stack", "stack.calloc(SIZEOF), null"),
];

const ALLOCATION_TYPES_BUFFER: [(&str, &str, &str); 5] = [
    ("malloc", "int capacity", "MemoryUtil.malloc(ALIGNOF, SIZEOF * capacity), capacity"),
    ("calloc", "int capacity", "MemoryUtil.calloc(ALIGNOF, SIZEOF * capacity), capacity"),
    ("create", "int capacity, long address", "address, capacity"),
    ("malloc", "int capacity, MemoryStack stack", "stack.malloc(ALIGNOF, SIZEOF * capacity), capacity"),
    ("calloc", "int capacity, MemoryStack stack", "stack.calloc(ALIGNOF, SIZEOF * capacity), capacity"),
];

struct JavaField {
    pub name: String,
    pub ty: String
}

fn get_java_type2(ty: &str) -> String {
    match ty {
        "u8" | "i8" => "ByteBuffer".to_string(),
        "u16" | "i16" => "ShortBuffer".to_string(),
        "u32" | "i32" => "IntBuffer".to_string(),
        "u64" | "i64" | "usize" => "LongBuffer".to_string(),
        "f32" => "FloatBuffer".to_string(),
        "f64" => "DoubleBuffer".to_string(),
        a if a.starts_with('*') => "PointerBuffer".to_string(),
        a => a.to_string()
    }
}

fn get_java_type(ty: &str) -> String {
    match ty {
        "u8" | "i8" => "byte".to_string(),
        "u16" | "i16" => "short".to_string(),
        "u32" | "i32" => "int".to_string(),
        "u64" | "i64" | "usize" => "long".to_string(),
        "f32" => "float".to_string(),
        "f64" => "double".to_string(),
        a if a.starts_with("* const") => get_java_type2(&a.replacen("* const ", "", 1)),
        a if a.starts_with("* mut") => get_java_type2(&a.replacen("* mut ", "", 1)),
        a => a.to_string()
    }
}

fn get_java_name(name: &str) -> String {
    let mut result = String::new();
    let mut uppercase_next = false;

    for c in name.chars() {
        if c == '_' {
            uppercase_next = true;
        } else if uppercase_next {
            result.extend(c.to_uppercase());
            uppercase_next = false;
        } else {
            result.push(c);
        }
    }

    result
}

fn get_size_info(ty: &str) -> String {
    match ty {
        "u8" | "i8" => "1".to_string(),
        "u16" | "i16" => "2".to_string(),
        "u32" | "i32" => "4".to_string(),
        "u64" | "i64" => "8".to_string(),
        "f32" => "4".to_string(),
        "f64" => "8".to_string(),
        "usize" => "POINTER_SIZE".to_string(),
        a if a.starts_with('*') => "POINTER_SIZE".to_string(),
        a => format!("{a}.SIZEOF, {a}.ALIGNOF")
    }
}

fn get_size_info_only(ty: &str) -> String {
    match ty {
        "u8" | "i8" => "1".to_string(),
        "u16" | "i16" => "2".to_string(),
        "u32" | "i32" => "4".to_string(),
        "u64" | "i64" | "usize" => "8".to_string(),
        "f32" => "4".to_string(),
        "f64" => "8".to_string(),
        a if a.starts_with('*') => "POINTER_SIZE".to_string(),
        a => format!("{a}.SIZEOF")
    }
}

fn get_java_name_and_type_resolved(java_fields: &Vec<JavaField>, field: &JavaField) -> (String, String) {
    let mut java_ty = get_java_type(&field.ty);
    let java_name = get_java_name(&field.name);

    const JAVA_BUFFERS: [&str; 7] = ["ByteBuffer", "ShortBuffer", "IntBuffer", "LongBuffer", "FloatBuffer", "DoubleBuffer", "PointerBuffer"];

    if field.name.starts_with("p_") &&
        !JAVA_BUFFERS.contains(&java_ty.as_str()) {
        let mut count_name = field.name.replace("p_", "");
        count_name.push_str("_count");

        if java_fields.iter().find(|i| i.name == count_name).is_some() {
            java_ty.push_str(".Buffer");
        }
    }

    (java_name, java_ty)
}

fn get_get2(ty: &str, addr: &str, capacity: Option<String>) -> String {
    match ty {
        "u8" | "i8" => format!("mem_byte_buffer({addr}, {cap})", cap = capacity.unwrap_or("1".to_string())),
        "u16" | "i16" => format!("mem_byte_buffer({addr}, {cap} * {s}).asShortBuffer()", s = get_size_info_only(ty), cap = capacity.unwrap_or("1".to_string())),
        "u32" | "i32" => format!("mem_byte_buffer({addr}, {cap} * {s}).asIntBuffer()", s = get_size_info_only(ty), cap = capacity.unwrap_or("1".to_string())),
        "u64" | "i64" | "usize" => format!("mem_byte_buffer({addr}, {cap} * {s}).asLongBuffer()", s = get_size_info_only(ty), cap = capacity.unwrap_or("1".to_string())),
        "f32" => format!("mem_byte_buffer({addr}, {cap} * {s}).asFloatBuffer()", s = get_size_info_only(ty), cap = capacity.unwrap_or("1".to_string())),
        "f64" => format!("mem_byte_buffer({addr}, {cap} * {s}).asDoubleBuffer()", s = get_size_info_only(ty), cap = capacity.unwrap_or("1".to_string())),
        a if a.starts_with('*') => panic!("no, im not implementing this"),
        a => {
            if let Some(cap) = capacity {
                format!("new {a}.Buffer({addr}, {cap})")
            } else {
                format!("{a}.create({addr})")
            }
        }
    }
}

fn get_get(ty: &str, addr: &str, capacity: Option<String>) -> String {
    match ty {
        "u8" | "i8" => format!("getByte({addr})"),
        "u16" | "i16" => format!("getShort({addr})"),
        "u32" | "i32" => format!("getInt({addr})"),
        "u64" | "i64" => format!("getLong({addr})"),
        "f32" => format!("getFloat({addr})"),
        "f64" => format!("getDouble({addr})"),
        "usize" => format!("getAddress({addr})"),
        a if a.starts_with("* const") => get_get2(&a.replacen("* const ", "", 1), addr, capacity),
        a if a.starts_with("* mut") => get_get2(&a.replacen("* mut ", "", 1), addr, capacity),
        a => panic!("What do i do with {a}")
    }
}

fn get_set2(ty: &str, addr: &str, value: &str) -> String {
    match ty {
        "u8" | "i8" |
        "u16" | "i16" |
        "u32" | "i32" |
        "u64" | "i64" | "usize" |
        "f32" |
        "f64" => format!("putAddress({addr}, memAddress({value}))"),
        a if a.starts_with('*') => panic!("no, im not implementing this"),
        _ => format!("putAddress({addr}, {value}.address())")
    }
}

fn get_set(ty: &str, addr: &str, value: &str) -> String {
    match ty {
        "u8" | "i8" => format!("putByte({addr}, {value})"),
        "u16" | "i16" => format!("putShort({addr}, {value})"),
        "u32" | "i32" => format!("putInt({addr}, {value})"),
        "u64" | "i64" => format!("putLong({addr}, {value})"),
        "f32" => format!("putFloat({addr}, {value})"),
        "f64" => format!("putDouble({addr}, {value})"),
        "usize" => format!("putAddress({addr}, {value})"),
        a if a.starts_with("* const") => get_set2(&a.replacen("* const ", "", 1), addr, value),
        a if a.starts_with("* mut") => get_set2(&a.replacen("* mut ", "", 1), addr, value),
        a => panic!("What do i do with {a}")
    }
}

#[proc_macro_attribute]
pub fn java_compatible(params: TokenStream, input: TokenStream) -> TokenStream {
    let java_struct = input.clone();
    let java_struct = parse_macro_input!(java_struct as DeriveInput);

    let mut package = None;

    let parser = parser(|meta| {
        if meta.path.is_ident("package") {
            package = Some(meta.value()?.parse::<LitStr>()?.value());
        } else {
            return Err(meta.error("unsupported argument"));
        }

        Ok(())
    });

    parse_macro_input!(params with parser);


    let name = java_struct.ident.to_string();

    let Data::Struct(data) = java_struct.data else { panic!("non structs are not java compatible!") };
    let Fields::Named(fields) = data.fields else { panic!("structs without named fields are not java compatible!") };
    let mut java_fields = Vec::with_capacity(fields.named.len());

    for field in fields.named {
        java_fields.push(JavaField { name: field.ident.unwrap().to_string(), ty: field.ty.into_token_stream().to_string() })
    }

    let package = if let Some(package) = package { format!("package {package};") } else { String::new() };

    let struct_fields = java_fields.iter()
        .map(| JavaField { name, ty } | format!(" *     {typ} {name}", typ = ty.replace("* const ", "*const ").replace("* mut ", "*mut ")))
        .collect::<Vec<_>>()
        .join(",\n");

    let offset_fields = java_fields.iter()
        .map(| JavaField { name, .. } | format!("                OF_{n}", n = name.to_uppercase()))
        .collect::<Vec<_>>()
        .join(",\n");

    let layout_fields = java_fields.iter()
        .map(| JavaField { ty, .. } | format!("                __member({member_info})", member_info = get_size_info(ty)))
        .collect::<Vec<_>>()
        .join(",\n");

    let mut offset_fields_set = String::new();
    for i in 0 .. java_fields.len() {
        let nl = (i != java_fields.len() - 1).yn("\n".to_string(), String::new());
        let mut name = "OF_".to_string();
        name.push_str(&java_fields[i].name.to_uppercase());
        offset_fields_set.push_str(&format!("        {name} = layout.offsetof({i});{nl}"))
    }

    let mut getters = String::new();
    for field in &java_fields {
        let (java_name, java_ty) = get_java_name_and_type_resolved(&java_fields, field);

        getters.push_str(&format!("    public {java_ty} {java_name}() {{ return n{java_name}(address()); }}\n\n"))
    }

    let mut setters = String::new();
    for field in &java_fields {
        let (java_name, java_ty) = get_java_name_and_type_resolved(&java_fields, field);

        let p_name = field.name.replace("_count", "");
        let p_name = format!("p_{p_name}");

        if java_fields.iter().find(|i| i.name == p_name).is_some() {
            continue;
        }

        setters.push_str(&format!("    public {name} {java_name}({java_ty} value) {{ n{java_name}(address(), value); return this; }}\n\n"))
    }

    let mut set = format!("    public {name} set(\n");
    for i in 0..java_fields.len() {
        let field = &java_fields[i];
        let (java_name, java_ty) = get_java_name_and_type_resolved(&java_fields, field);

        let comma = (i != 0).yn(",\n", "");

        let p_name = field.name.replace("_count", "");
        let p_name = format!("p_{p_name}");

        if java_fields.iter().find(|i| i.name == p_name).is_some() {
            continue;
        }

        set.push_str(&format!("{comma}        {java_ty} {java_name}"));
    }
    set.push_str("\n    ) {\n");
    for field in &java_fields {
        let (java_name, _java_ty) = get_java_name_and_type_resolved(&java_fields, field);

        let p_name = field.name.replace("_count", "");
        let p_name = format!("p_{p_name}");

        if java_fields.iter().find(|i| i.name == p_name).is_some() {
            continue;
        }

        set.push_str(&format!("        {java_name}({java_name});\n"));
    }
    set.push_str("\n        return this;\n    }\n\n");
    set.push_str(&format!("    public {name} set({name} src) {{\n        mem_cpy(address(), src.address(), SIZEOF);\n        return this;\n    }}\n\n"));

    let mut allocations = String::new();
    for (a, b, c) in ALLOCATION_TYPES {
        allocations.push_str(&format!("    public static {name} {a}({b}) {{\n        return new {name}({c});\n    }}\n\n"));
    }

    for (a, b, c) in ALLOCATION_TYPES_BUFFER {
        allocations.push_str(&format!("    public static {name}.Buffer {a}({b}) {{\n        return new {name}.Buffer({c});\n    }}\n\n"));
    }

    let mut ngetters = String::new();
    for field in &java_fields {
        let (java_name, java_ty) = get_java_name_and_type_resolved(&java_fields, field);
        let upper = format!("OF_{n}", n = field.name.to_uppercase());

        let mut count_name = field.name.replace("p_", "");
        count_name.push_str("_count");

        let cap = java_fields.iter().find(|i| i.name == count_name);
        let cap = cap.map(|field|
            format!("n{arg}(struct)", arg=get_java_name(&field.name))
        );

        let f = get_get(&field.ty, &format!("struct + {name}.{upper}"), cap);

        ngetters.push_str(&format!("    public static {java_ty} n{java_name}(long struct) {{ return {f}; }}\n\n"))
    }

    let mut nsetters = String::new();
    for field in &java_fields {
        let (java_name, java_ty) = get_java_name_and_type_resolved(&java_fields, field);
        let upper = format!("OF_{n}", n = field.name.to_uppercase());

        let mut count_name = field.name.replace("p_", "");
        count_name.push_str("_count");

        let cap = java_fields.iter().find(|i| i.name == count_name);
        let cap = cap.map(|field| get_java_name(&field.name));

        let mut count_setter = String::new();
        if let Some(cap) = cap {
            count_setter = format!("n{cap}(struct, value == null ? 0 : value.remaining()); ");
        }

        let f = get_set(&field.ty, &format!("struct + {name}.{upper}"), "value");

        nsetters.push_str(&format!("    public static void n{java_name}(long struct, {java_ty} value) {{ {f}; {count_setter}}}\n\n"))
    }

    let class = format!(r#"{package}

import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryStack;
import net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil;
import net.xoknox.czernicesweeper.czerniceaudio.mem.Struct;
import net.xoknox.czernicesweeper.czerniceaudio.mem.StructBuffer;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static net.xoknox.czernicesweeper.czerniceaudio.mem.MemoryUtil.*;

/**
 *  {name} Java Struct
 *
 *  <pre><code>
 *  struct {name} {{
{struct_fields}
 *  }}</code></pre>
 */
public class {name} extends Struct<{name}> {{

    public static final int SIZEOF, ALIGNOF;

    protected static final int
{offset_fields};

    static {{
        Layout layout = __struct(
{layout_fields}
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

{offset_fields_set}
    }}

    protected {name}(long address, @Nullable ByteBuffer container) {{
        super(address, container);
    }}

    @Override
    protected {name} create(long address, @Nullable ByteBuffer container) {{
        return new {name}(address, container);
    }}

    public {name}(ByteBuffer container) {{
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }}

    @Override
    public int sizeof() {{ return SIZEOF; }}

{getters}
{setters}
{set}
{allocations}
{ngetters}
{nsetters}
    public static class Buffer extends StructBuffer<{name}, Buffer> {{
        private static final {name} ELEMENT_FACTORY = {name}.create(-1L);

        public Buffer(ByteBuffer container) {{
            super(container, container.remaining() / SIZEOF);
        }}

        public Buffer(long address, int cap) {{
            super(address, cap);
        }}

        @Override
        protected Buffer self() {{ return this; }}

        @Override
        protected Buffer create(long address, int capacity) {{
            return new Buffer(address, capacity);
        }}

        @Override
        public {name} getElementFactory() {{
            return ELEMENT_FACTORY;
        }}
    }}
}}"#);

    let mut file = OpenOptions::new().write(true).truncate(true).create(true).open(format!("generated/{name}.java")).unwrap();

    file.write_all(class.as_bytes()).unwrap();

    //file.write_all(class.as_bytes()).unwrap();

    input
}