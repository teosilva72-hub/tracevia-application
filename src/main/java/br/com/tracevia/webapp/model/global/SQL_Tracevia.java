package br.com.tracevia.webapp.model.global;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class SQL_Tracevia {
	private interface SQL_Interface extends Library {
	    boolean start(int conn);
	    Void prepare(String query);
	    Pointer execute_json();
	    Pointer execute();
	}
	
	private SQL_Interface inner = Native.load("C:\\t\\sql_service.dll", SQL_Interface.class);

	public class MapJsonResult implements Iterable<RowJsonResult> {
        private JsonArray result;

        MapJsonResult(JsonElement json) throws IOException {
            if (json.isJsonArray())
            	result = json.getAsJsonArray();
            else
            	throw new IOException("Not is valid json array");
        }

		@Override
		public Iterator<RowJsonResult> iterator() {
			return new IteratorJsonRow(result.iterator());
		}
    }
    
    public class IteratorJsonRow implements Iterator<RowJsonResult> {
        private Iterator<JsonElement> iterator;
        private JsonObject next;

        IteratorJsonRow(Iterator<JsonElement> iterator) {
        	this.iterator = iterator;
        }

		@Override
		public boolean hasNext() {
            if (iterator.hasNext() && next == null) {
                JsonElement next = iterator.next();
                if (next.isJsonObject())
	        		this.next = next.getAsJsonObject();
            }
			return this.next != null;
		}

		@Override
		public RowJsonResult next() {
			if (!this.hasNext())
				return null;
			
        	JsonObject next = this.next;
            this.next = null;
            return new RowJsonResult(next);
		}
    }
    
    public class RowJsonResult {
        private JsonObject current;
        private String[] keys;

        RowJsonResult(JsonObject current) {
        	int size = current.size();
        	Object[] set = current.keySet().toArray();
        	
        	this.keys = new String[size];
        	for (int i = 0; i < size; i++)
        		keys[i] = (String) set[i];
        	this.current = current;
        }

        public String getString(int integer) throws Exception {
        	return current.get(keys[integer - 1]).getAsString();
        }
        
		public String getString(String string) throws Exception {
			return current.get(string).getAsString();
		}
		
		public int getInt(int integer) throws Exception {
			return current.get(keys[integer - 1]).getAsInt();
		}
		
		public int getInt(String string) throws Exception {
			return current.get(string).getAsInt();
		}
		
		public boolean getBoolean(int integer) throws Exception {
			return current.get(keys[integer - 1]).getAsBoolean();
		}
		
		public boolean getBoolean(String string) throws Exception {
			return current.get(string).getAsBoolean();
		}
		
		public double getDouble(int integer) throws Exception {
			return current.get(keys[integer - 1]).getAsDouble();
		}
		
		public double getDouble(String string) throws Exception {
			return current.get(string).getAsDouble();
		}
		
		public float getFloat(int integer) throws Exception {
			return current.get(keys[integer - 1]).getAsFloat();
		}
		
		public float getFloat(String string) throws Exception {
			return current.get(string).getAsFloat();
		}
    }
    
    public class Result extends Structure {    	
    	public int size;
    	public Pointer ptr;
    	
    	Result(Pointer ptr) {
    		super(ptr);
    		read();
    	}
    	
    	MapResult to_map() throws IOException {
    		Pointer[] pointers = ptr.getPointerArray(0, size);
    		RowResult[] columns = new RowResult[size];
    		
    		for (int i = 0; i < size; i++) {
    			columns[i] = new ColumnsResult(pointers[i]).to_row();
    		}
    		
    		return new MapResult(columns);
    	}
    	
    	@Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] {"size","ptr"});
        }
    }
    
    public class ColumnsResult extends Structure {
    	public int size;
    	public Pointer ptr;
    	
    	ColumnsResult(Pointer ptr) {
    		super(ptr);
    		read();
    	}
    	
    	@Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] {"size","ptr"});
        }

		public RowResult to_row() {
			return new RowResult(ptr, size);
		}
    }
    
    public class MapResult implements Iterable<RowResult> {
        private RowResult[] result;

        MapResult(RowResult[] result) throws IOException {
        	this.result = result;
        }

		@Override
		public Iterator<RowResult> iterator() {
			return Arrays.asList(result).listIterator();
		}
    }
    
    public class Row extends Structure {
    	public Pointer value;
    	public int size;
    	public byte type;
    	public String key;
    	
    	Row(Pointer ptr) {
    		super(ptr);
    		read();
    	}
    	
    	private byte[] getBytes() {
    		return value.getByteArray(0, size);
    	}
    	
    	@Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] {"value", "size", "type","key"});
        }
    }
    
    public class RowResult {
        private Row[] cols;
        private HashMap<String, Integer> map;

        RowResult(Pointer current, int size) {
        	Row[] rows = new Row[size];
        	HashMap<String, Integer> map = new HashMap<String, Integer>();
        	Pointer[] ptr = current.getPointerArray(0, size);
        	
        	for (int i = 0; i < size; i++) {
    			rows[i] = new Row(ptr[i]);
    			map.put(rows[i].key, i);
    		}
        	
        	this.cols = rows;
        	this.map = map;
        }
        
        public String getString(int integer) throws Exception {
        	return new String(cols[integer - 1].getBytes(), StandardCharsets.UTF_8);
        }
        
        public String getString(String string) throws Exception {
        	return new String(cols[map.get(string)].getBytes(), StandardCharsets.UTF_8);
        }
        
        public int getInt(int integer) throws Exception {
        	Row row = cols[integer - 1];
        	if (row.type == 1)
        		return Integer.parseInt(new String(row.getBytes(), StandardCharsets.UTF_8));
        	else
        		return ByteBuffer.wrap(Arrays.copyOfRange(row.getBytes(), 0, 4)).getInt();
        }
        
        public int getInt(String string) throws Exception {
        	Row row = cols[map.get(string)];
        	if (row.type == 1)
        		return Integer.parseInt(new String(row.getBytes(), StandardCharsets.UTF_8));
        	else
        		return ByteBuffer.wrap(Arrays.copyOfRange(row.getBytes(), 0, 4)).getInt();
        }
        
        public byte[] getBytes(int integer) throws Exception {
        	return cols[integer - 1].getBytes();
        }
        
        public byte[] getBytes(String string) throws Exception {
        	return cols[map.get(string)].getBytes();
        }
        
    }
	
	public boolean start(int conn) {
		return inner.start(conn);
	}
	
	public Void prepare(String query) {
		return inner.prepare(query);
	}
	
	public MapJsonResult execute_json() throws IOException {
		Pointer ptr = inner.execute_json();
		JsonElement result = ptr != null ? new Gson().fromJson(ptr.getString(0, "utf8"), JsonElement.class) : null;

		return ptr != null ? new MapJsonResult(result) : null;
	}
	
	public MapResult execute() throws IOException {
		Result result = new Result(inner.execute());
		
		return result.to_map();
	}
}