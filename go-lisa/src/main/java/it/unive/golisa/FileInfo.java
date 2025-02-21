package it.unive.golisa;

import java.util.Objects;

class FileInfo {
	
    private final String input;
    private final String contractname;
    private final String channel;

    public FileInfo(String input, String contractname, String channel) {
        this.input = input;
        this.contractname = contractname;
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Input File: " + input + ", Contract Name: " + contractname + ", Channel: " + channel;
    }

	public String getInput() {
		return input;
	}

	public String getContractName() {
		return contractname;
	}

	public String getChannel() {
		return channel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channel, input, contractname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		return Objects.equals(channel, other.channel) && Objects.equals(input, other.input)
				&& Objects.equals(contractname, other.contractname);
	}
    
	
}