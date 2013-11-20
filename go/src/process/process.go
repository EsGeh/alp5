package process


type Process interface{
	GetPeers() []Process
	Start(peers []Process)
	Send(message Any)
	Recv() Any
}

type Any interface{}

type ProcessImpl struct {
	mailbox chan Any
	peers []Process
}

func New( name string, size uint) (proc *ProcessImpl) {
	mailbox := make(chan Any, size)
	proc = & ProcessImpl{ 
		mailbox,
		nil,
	}
	return
}

func (this *ProcessImpl) GetPeers() ([]Process) {
	return this.peers
}

func (this* ProcessImpl) Start(peers []Process) {
	this.peers = peers
}


func (this* ProcessImpl) Send(message Any) {
	this.mailbox <- message
}

func (this* ProcessImpl) Recv() Any {
	return <- this.mailbox
}
